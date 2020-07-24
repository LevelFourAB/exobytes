package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.Token;

public class DoubleSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Double> serializer = new DoubleSerializer();

		StreamingInput in = write(out -> serializer.write(3.14, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(3.14));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Double> serializer = new DoubleSerializer();

		StreamingInput in = write(out -> out.writeDouble(3.14))
			.get();

		Double v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(3.14));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(DoubleSerializer.class).verify();
	}
}
