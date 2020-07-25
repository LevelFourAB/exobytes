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

public class ByteArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWriteEmpty()
		throws IOException
	{
		Serializer<byte[]> serializer = new ByteArraySerializer();

		StreamingInput in = write(out -> serializer.write(new byte[0], out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[0]));
	}

	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<byte[]> serializer = new ByteArraySerializer();

		StreamingInput in = write(out -> serializer.write(new byte[] { 0x01, 0x02, 0x03 }, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[] { 0x01, 0x02, 0x03 }));
	}

	@Test
	public void testReadEmpty()
		throws IOException
	{
		Serializer<byte[]> serializer = new ByteArraySerializer();

		StreamingInput in = write(out -> out.writeBytes(new byte[0]))
			.get();

		byte[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new byte[0]));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<byte[]> serializer = new ByteArraySerializer();

		StreamingInput in = write(out -> out.writeBytes(new byte[] { 0x01, 0x02, 0x03 }))
			.get();

		byte[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new byte[] { 0x01, 0x02, 0x03 }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(ByteArraySerializer.class).verify();
	}
}
