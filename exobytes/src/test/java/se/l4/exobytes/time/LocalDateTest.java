package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.ZoneOffset;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class LocalDateTest
	extends SerializerTest
{
	protected Serializer<LocalDate> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(LocalDate.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<LocalDate> serializer = serializer(TemporalHints.timestamp());

		LocalDate date = LocalDate.now();
		StreamingInput in = write(out -> serializer.write(date, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(date.atTime(0, 0).toInstant(ZoneOffset.UTC).toEpochMilli()));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<LocalDate> serializer = serializer(TemporalHints.timestamp());

		LocalDate date = LocalDate.now();
		StreamingInput in = write(out -> out.writeLong(date.toEpochDay() * 86400000l)).get();

		LocalDate value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(date));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<LocalDate> serializer = serializer(
			TemporalHints.format()
		);

		LocalDate dt = LocalDate.of(2011, 12, 3);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<LocalDate> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03")).get();
		LocalDate value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalDate.of(2011, 12, 3)));
	}
}
