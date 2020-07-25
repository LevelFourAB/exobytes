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

public class IntSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Integer> serializer = new IntSerializer();

		StreamingInput in = write(out -> serializer.write(8291, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(8291));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Integer> serializer = new IntSerializer();

		StreamingInput in = write(out -> out.writeInt(8291))
			.get();

		Integer v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(8291));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(IntSerializer.class).verify();
	}
}
