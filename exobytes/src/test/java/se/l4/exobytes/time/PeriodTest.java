package se.l4.exobytes.time;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.time.Period;

import org.eclipse.collections.api.factory.Lists;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class PeriodTest
	extends SerializerTest
{
	protected Serializer<Period> serializer(Annotation... annotations)
	{
		Serializers serializers = emptySerializers();
		new TimeSerializersModule().activate(serializers);

		return serializers.get(Period.class, Lists.immutable.of(annotations));
	}

	@Test
	public void testWriteFormatted()
		throws IOException
	{
		Serializer<Period> serializer = serializer(
			TemporalHints.format()
		);

		Period d = Period.ofDays(2);
		StreamingInput in = write(out -> serializer.write(d, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("P2D"));
	}

	@Test
	public void testReadFormatted()
		throws IOException
	{
		Serializer<Period> serializer = serializer(
			TemporalHints.format()
		);

		StreamingInput in = write(out -> out.writeString("P2D")).get();

		Period value = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(value, is(Period.ofDays(2)));
	}
}
