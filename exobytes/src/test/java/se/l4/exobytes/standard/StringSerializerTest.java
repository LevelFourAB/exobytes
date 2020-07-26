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

public class StringSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<String> serializer = new StringSerializer();

		StreamingInput in = write(out -> serializer.write("\ud800\udd51", out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("\ud800\udd51"));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<String> serializer = new StringSerializer();

		StreamingInput in = write(out -> out.writeString("\ud800\udd51"))
			.get();

		String v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is("\ud800\udd51"));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(StringSerializer.class).verify();
	}
}
