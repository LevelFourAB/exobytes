package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class LongSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Long> serializer = new LongSerializer();

		StreamingInput in = write(out -> serializer.write(1000000000000l, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(1000000000000l));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Long> serializer = new LongSerializer();

		StreamingInput in = write(out -> out.writeLong(1000000000000l))
			.get();

		Long v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(1000000000000l));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(LongSerializer.class).verify();
	}
}
