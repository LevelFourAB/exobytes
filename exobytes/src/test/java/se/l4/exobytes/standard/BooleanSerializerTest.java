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

public class BooleanSerializerTest
	extends SerializerTest
{
	@Test
	public void testWriteTrue()
		throws IOException
	{
		Serializer<Boolean> serializer = new BooleanSerializer();

		StreamingInput in = write(out -> serializer.write(true, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(true));
	}

	@Test
	public void testWriteFalse()
		throws IOException
	{
		Serializer<Boolean> serializer = new BooleanSerializer();

		StreamingInput in = write(out -> serializer.write(false, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(false));
	}

	@Test
	public void testReadTrue()
		throws IOException
	{
		Serializer<Boolean> serializer = new BooleanSerializer();

		StreamingInput in = write(out -> out.writeBoolean(true))
			.get();

		Boolean v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(true));
	}

	@Test
	public void testReadFalse()
		throws IOException
	{
		Serializer<Boolean> serializer = new BooleanSerializer();

		StreamingInput in = write(out -> out.writeBoolean(false))
			.get();

		Boolean v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(false));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(BooleanSerializer.class).verify();
	}
}
