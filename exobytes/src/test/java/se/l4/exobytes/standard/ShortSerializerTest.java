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

public class ShortSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Short> serializer = new ShortSerializer();

		StreamingInput in = write(out -> serializer.write((short) 17000, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readShort(), is((short) 17000));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Short> serializer = new ShortSerializer();

		StreamingInput in = write(out -> out.writeShort((short) 17000))
			.get();

		Short v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is((short) 17000));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(ShortSerializer.class).verify();
	}
}
