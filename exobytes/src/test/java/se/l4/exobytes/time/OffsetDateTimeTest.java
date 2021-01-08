package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class OffsetDateTimeTest
	extends SerializerTest
{
	protected Serializer<OffsetDateTime> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(OffsetDateTime.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> serializer.write(OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(1322907330000l));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(TemporalAnnotations.timestamp());

		StreamingInput in = write(out -> out.writeLong(1322907330000l)).get();

		OffsetDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is((OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC))));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		OffsetDateTime time = OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.ofHours(2));
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03T10:15:30+02:00"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format()
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03T10:15:30+02:00")).get();
		OffsetDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.ofHours(2))));
	}

	@Test
	public void testWriteFormatIsoDate()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_DATE)
		);

		OffsetDateTime time = OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03Z"));
	}

	@Test
	public void testReadFormatIsoDate()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_DATE)
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03Z")).get();
		OffsetDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(OffsetDateTime.of(2011, 12, 3, 0, 0, 0, 0, ZoneOffset.UTC)));
	}

	@Test
	public void testWriteFormatIsoTime()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_TIME)
		);

		OffsetDateTime time = OffsetDateTime.of(2011, 12, 3, 10, 15, 30, 0, ZoneOffset.UTC);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("10:15:30Z"));
	}

	@Test
	public void testReadFormatIsoTime()
		throws IOException
	{
		Serializer<OffsetDateTime> serializer = serializer(
			TemporalAnnotations.format(Temporal.StandardFormat.ISO_TIME)
		);

		StreamingInput in = write(out -> out.writeString("10:15:30Z")).get();
		OffsetDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalTime.of(10, 15, 30).atDate(LocalDate.now()).atOffset(ZoneOffset.UTC)));
	}
}
