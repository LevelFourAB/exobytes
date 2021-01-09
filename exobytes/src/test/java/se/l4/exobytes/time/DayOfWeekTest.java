package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.DayOfWeek;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class DayOfWeekTest
	extends SerializerTest
{
	protected Serializer<DayOfWeek> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(DayOfWeek.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteDefault()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer();

		StreamingInput in = write(out -> serializer.write(DayOfWeek.FRIDAY, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(5));
	}

	@Test
	public void testReadDefault()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer();

		StreamingInput in = write(out -> out.writeLong(5)).get();

		DayOfWeek value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(DayOfWeek.FRIDAY));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> serializer.write(DayOfWeek.FRIDAY, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("5"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("5")).get();

		DayOfWeek value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(DayOfWeek.FRIDAY));
	}

	@Test
	public void testWriteFormatCustom()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer(
			TemporalHints.customFormat("EE")
		);

		StreamingInput in = write(out -> serializer.write(DayOfWeek.FRIDAY, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("Fri"));
	}

	@Test
	public void testReadFormatCustom()
		throws IOException
	{
		Serializer<DayOfWeek> serializer = serializer(
			TemporalHints.customFormat("EE")
		);

		StreamingInput in = write(out -> out.writeString("Fri")).get();

		DayOfWeek value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(DayOfWeek.FRIDAY));
	}
}
