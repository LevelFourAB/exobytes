package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.commons.io.Bytes;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class BytesSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<Bytes> serializer = new BytesSerializer();

		StreamingInput in = write(out -> serializer.write(Bytes.create(new byte[] { 0x01, 0x02 }), out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readByteArray(), is(new byte[] { 0x01, 0x02 }));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<Bytes> serializer = new BytesSerializer();

		StreamingInput in = write(out -> out.writeBytes(new byte[] { 0x01, 0x02 }))
			.get();

		Bytes v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v.toByteArray(), is(new byte[] { 0x01, 0x02 }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(BytesSerializer.class).verify();
	}
}
