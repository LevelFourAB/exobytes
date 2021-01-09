package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class LocalDateTimeTest
	extends SerializerTest
{
	protected Serializer<LocalDateTime> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(LocalDateTime.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteTimestampDefault()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(TemporalHints.timestamp());

		StreamingInput in = write(out -> serializer.write(LocalDateTime.of(2011, 12, 3, 10, 15, 30), out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(1322907330000l));
	}

	@Test
	public void testReadTimestampDefault()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(TemporalHints.timestamp());

		StreamingInput in = write(out -> out.writeLong(1322907330000l)).get();

		LocalDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is((LocalDateTime.of(2011, 12, 3, 10, 15, 30))));
	}

	@Test
	public void testWriteFormatDefault()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format()
		);

		LocalDateTime time = LocalDateTime.of(2011, 12, 3, 10, 15, 30);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03T10:15:30"));
	}

	@Test
	public void testReadFormatDefault()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03T10:15:30")).get();
		LocalDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalDateTime.of(2011, 12, 3, 10, 15, 30)));
	}

	@Test
	public void testWriteFormatIsoDate()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format(TemporalHints.StandardFormat.ISO_DATE)
		);

		LocalDateTime time = LocalDateTime.of(2011, 12, 3, 10, 15, 30);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("2011-12-03"));
	}

	@Test
	public void testReadFormatIsoDate()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format(TemporalHints.StandardFormat.ISO_DATE)
		);

		StreamingInput in = write(out -> out.writeString("2011-12-03")).get();
		LocalDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalDateTime.of(2011, 12, 3, 0, 0, 0)));
	}

	@Test
	public void testWriteFormatIsoTime()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format(TemporalHints.StandardFormat.ISO_TIME)
		);

		LocalDateTime time = LocalDateTime.of(2011, 12, 3, 10, 15, 30);
		StreamingInput in = write(out -> serializer.write(time, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("10:15:30"));
	}

	@Test
	public void testReadFormatIsoTime()
		throws IOException
	{
		Serializer<LocalDateTime> serializer = serializer(
			TemporalHints.format(TemporalHints.StandardFormat.ISO_TIME)
		);

		StreamingInput in = write(out -> out.writeString("10:15:30")).get();
		LocalDateTime value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(LocalTime.of(10, 15, 30).atDate(LocalDate.now())));
	}
}
