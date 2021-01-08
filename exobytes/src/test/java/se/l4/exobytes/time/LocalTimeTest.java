package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalTime;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class LocalTimeTest
	extends SerializerTest
{
	protected Serializer<LocalTime> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(LocalTime.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<LocalTime> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> serializer.write(LocalTime.of(10, 15, 30), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(36930000l));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<LocalTime> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> out.writeLong(36930000)).get();

		LocalTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is((LocalTime.of(10, 15, 30))));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<LocalTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		LocalTime time = LocalTime.of(10, 15, 30);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("10:15:30"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<LocalTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		StreamingInput in = write(out -> out.writeString("10:15:30")).get();
		LocalTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalTime.of(10, 15, 30)));
	}
}
