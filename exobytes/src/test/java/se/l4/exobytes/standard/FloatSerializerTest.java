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

public class FloatSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Float> serializer = new FloatSerializer();

		StreamingInput in = write(out -> serializer.write(3.14f, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readFloat(), is(3.14f));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Float> serializer = new FloatSerializer();

		StreamingInput in = write(out -> out.writeFloat(3.14f))
			.get();

		Float v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(3.14f));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(FloatSerializer.class).verify();
	}
}
