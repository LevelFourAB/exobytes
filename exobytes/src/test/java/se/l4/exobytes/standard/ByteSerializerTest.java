package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class ByteSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Byte> serializer = new ByteSerializer();

		StreamingInput in = write(out -> serializer.write((byte) 200, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByte(), is((byte) 200));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Byte> serializer = new ByteSerializer();

		StreamingInput in = write(out -> out.writeByte((byte) 200))
			.get();

		Byte v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is((byte) 200));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(ByteSerializer.class).verify();
	}
}
