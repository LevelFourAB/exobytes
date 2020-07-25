package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.UUID;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.Token;

public class UuidSerializerTest extends SerializerTest {
	@Test
	public void testWrite() throws IOException {
		Serializer<UUID> serializer = new UuidSerializer();

		UUID value = UUID.fromString("29fa14b7-6fb6-4d68-bec1-40307a949421");
		StreamingInput in = write(out -> serializer.write(value, out)).get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(Hex.encodeHexString(in.readByteArray()), is("29fa14b76fb64d68bec140307a949421"));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<UUID> serializer = new UuidSerializer();

		StreamingInput in = write(out -> {
			try
			{
				out.writeBytes(Hex.decodeHex("29fa14b76fb64d68bec140307a949421"));
			}
			catch(DecoderException e)
			{
				throw new IOException(e);
			}
		}).get();

		UUID v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(UUID.fromString("29fa14b7-6fb6-4d68-bec1-40307a949421")));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(UuidSerializer.class).verify();
	}
}
