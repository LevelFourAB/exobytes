package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Month;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class MonthTest
	extends SerializerTest
{
	protected Serializer<Month> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(Month.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteDefault()
		throws IOException
	{
		Serializer<Month> serializer = serializer();

		StreamingInput in = write(out -> serializer.write(Month.AUGUST, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(8));
	}

	@Test
	public void testReadDefault()
		throws IOException
	{
		Serializer<Month> serializer = serializer();

		StreamingInput in = write(out -> out.writeLong(8)).get();

		Month value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Month.AUGUST));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<Month> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> serializer.write(Month.AUGUST, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("08"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<Month> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("08")).get();

		Month value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Month.AUGUST));
	}

	@Test
	public void testWriteFormatCustom()
		throws IOException
	{
		Serializer<Month> serializer = serializer(
			TemporalHints.customFormat("M")
		);

		StreamingInput in = write(out -> serializer.write(Month.AUGUST, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("8"));
	}

	@Test
	public void testReadFormatCustom()
		throws IOException
	{
		Serializer<Month> serializer = serializer(
			TemporalHints.customFormat("M")
		);

		StreamingInput in = write(out -> out.writeString("8")).get();

		Month value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Month.AUGUST));
	}
}
