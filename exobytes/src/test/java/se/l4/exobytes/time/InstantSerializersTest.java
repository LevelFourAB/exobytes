package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class InstantSerializersTest
	extends SerializerTest
{
	protected Serializer<Instant> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(Instant.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(TemporalHints.timestamp());

		Instant instant = Instant.now();
		StreamingInput in = write(out -> serializer.write(instant, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(instant.toEpochMilli()));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(TemporalHints.timestamp());

		Instant instant = Instant.now();
		StreamingInput in = write(out -> out.writeLong(instant.toEpochMilli())).get();

		Instant value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value.toEpochMilli(), is(instant.toEpochMilli()));
	}

	@Test
	public void testWriteTimestampSeconds()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(
			TemporalHints.timestamp(),
			TemporalHints.precision(ChronoUnit.SECONDS)
		);

		Instant instant = Instant.now();
		StreamingInput in = write(out -> serializer.write(instant, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(instant.getEpochSecond()));
	}

	@Test
	public void testReadTimestampSeconds()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(
			TemporalHints.timestamp(),
			TemporalHints.precision(ChronoUnit.SECONDS)
		);

		Instant instant = Instant.now();
		StreamingInput in = write(out -> out.writeLong(instant.getEpochSecond())).get();

		Instant value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value.getEpochSecond(), is(instant.getEpochSecond()));
	}

	@Test
	public void testWriteTimestampDays()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(
			TemporalHints.timestamp(),
			TemporalHints.precision(ChronoUnit.DAYS)
		);

		Instant instant = Instant.now();
		StreamingInput in = write(out -> serializer.write(instant, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(instant.getEpochSecond() / 86400));
	}

	@Test
	public void testReadTimestampDays()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(
			TemporalHints.timestamp(),
			TemporalHints.precision(ChronoUnit.DAYS)
		);

		Instant instant = Instant.now();
		StreamingInput in = write(out -> out.writeLong(instant.getEpochSecond() / 86400)).get();

		Instant value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value.getEpochSecond() / 86400, is(instant.getEpochSecond() / 86400));
	}

	@Test
	public void testTimestampEquality()
	{
		EqualsVerifier.forClass(TemporalSerializers.TimestampImpl.class).verify();
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<Instant> serializer = serializer(
			TemporalHints.format(),
			TemporalHints.precision(ChronoUnit.NANOS)
		);

		Instant instant = Instant.now();
		StreamingInput in = write(out -> serializer.write(instant, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is(instant.toString()));
	}
}
