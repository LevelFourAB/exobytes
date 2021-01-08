package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class ZonedDateTimeTest
	extends SerializerTest
{
	protected Serializer<ZonedDateTime> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(ZonedDateTime.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(TemporalAnnotations.timestamp());

		ZonedDateTime dt = ZonedDateTime.now(ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(dt.toInstant().toEpochMilli()));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(TemporalAnnotations.timestamp());

		ZonedDateTime dt = ZonedDateTime.now(ZoneOffset.UTC);
		StreamingInput in = write(out -> out.writeLong(dt.toInstant().toEpochMilli())).get();

		ZonedDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value.toInstant().toEpochMilli(), is(dt.toInstant().toEpochMilli()));
	}

	@Test
	public void testWriteTimestampSeconds()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.timestamp(),
			TemporalAnnotations.precision(ChronoUnit.SECONDS)
		);

		ZonedDateTime dt = ZonedDateTime.now(ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(dt.toEpochSecond()));
	}

	@Test
	public void testReadTimestampSeconds()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.timestamp(),
			TemporalAnnotations.precision(ChronoUnit.SECONDS)
		);

		ZonedDateTime dt = ZonedDateTime.now(ZoneOffset.UTC);
		StreamingInput in = write(out -> out.writeLong(dt.toEpochSecond())).get();

		ZonedDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value.toEpochSecond(), is(value.toEpochSecond()));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		ZonedDateTime dt = ZonedDateTime.of(2011, 12, 03, 10, 15, 30, 0, ZoneId.of("Europe/Paris"));
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03T10:15:30+01:00[Europe/Paris]"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03T10:15:30+01:00[Europe/Paris]")).get();

		ZonedDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(ZonedDateTime.of(2011, 12, 03, 10, 15, 30, 0, ZoneId.of("Europe/Paris"))));
	}

	@Test
	public void testWriteFormatIsoLocalDate()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_LOCAL_DATE)
		);

		ZonedDateTime dt = LocalDate.of(2011, 12, 3).atStartOfDay(ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03"));
	}

	@Test
	public void testReadFormatIsoLocalDate()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_LOCAL_DATE)
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03")).get();
		ZonedDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalDate.of(2011, 12, 3).atStartOfDay(ZoneOffset.UTC)));
	}

	@Test
	public void testWriteFormatIsoLocalTime()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_LOCAL_TIME)
		);

		ZonedDateTime dt = LocalDate.of(2011, 12, 3).atTime(10, 15, 30).atZone(ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(dt, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("10:15:30"));
	}

	@Test
	public void testReadFormatIsoLocalTime()
		throws IOException
	{
		Serializer<ZonedDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_LOCAL_TIME)
		);

		StreamingInput in = write(out -> out.writeString("10:15:30")).get();
		ZonedDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalDate.now(ZoneOffset.UTC).atTime(10, 15, 30).atZone(ZoneOffset.UTC)));
	}
}
