package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.OffsetTime;
import java.time.ZoneOffset;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class OffsetDateTest
	extends SerializerTest
{
	protected Serializer<OffsetTime> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(OffsetTime.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<OffsetTime> serializer = serializer(TemporalHints.timestamp());

		StreamingInput in = write(out -> serializer.write(OffsetTime.of(10, 15, 30, 0, ZoneOffset.UTC), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(36930000L));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<OffsetTime> serializer = serializer(TemporalHints.timestamp());

		StreamingInput in = write(out -> out.writeLong(36930000L)).get();

		OffsetTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is((OffsetTime.of(10, 15, 30, 0, ZoneOffset.UTC))));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<OffsetTime> serializer = serializer(
			TemporalHints.format()
		);

		OffsetTime time = OffsetTime.of(10, 15, 30, 0, ZoneOffset.ofHours(2));
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("10:15:30+02:00"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<OffsetTime> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("10:15:30+02:00")).get();
		OffsetTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(OffsetTime.of(10, 15, 30, 0, ZoneOffset.ofHours(2))));
	}
}
