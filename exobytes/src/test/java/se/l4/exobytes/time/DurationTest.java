package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Duration;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class DurationTest
	extends SerializerTest
{
	protected Serializer<Duration> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(Duration.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteDefault()
		throws IOException
	{
		Serializer<Duration> serializer = serializer();

		Duration d = Duration.ofDays(2);
		StreamingInput in = write(out -> serializer.write(d, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(d.toMillis()));
	}

	@Test
	public void testReadDefault()
		throws IOException
	{
		Serializer<Duration> serializer = serializer();

		Duration d = Duration.ofDays(2);
		StreamingInput in = write(out -> out.writeLong(d.toMillis())).get();

		Duration value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(d));
	}

	@Test
	public void testWriteFormatted()
		throws IOException
	{
		Serializer<Duration> serializer = serializer(
			TemporalHints.format()
		);

		Duration d = Duration.ofDays(2);
		StreamingInput in = write(out -> serializer.write(d, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("PT48H"));
	}

	@Test
	public void testReadFormatted()
		throws IOException
	{
		Serializer<Duration> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("PT48H")).get();

		Duration value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Duration.ofDays(2)));
	}
}
