package se.l4.exobytes.array;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class BooleanArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<boolean[]> serializer = new BooleanArraySerializer();

		StreamingInput in = write(out -> serializer.write(new boolean[] { true, false }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(true));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readBoolean(), is(false));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<boolean[]> serializer = new BooleanArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeBoolean(true);
			out.writeBoolean(false);
			out.writeListEnd();
		})
			.get();

		boolean[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new boolean[] { true, false }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<boolean[]> serializer = new BooleanArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeBoolean(true);
			out.writeBoolean(false);
			out.writeListEnd();
		})
			.get();

		boolean[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new boolean[] { true, false }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(BooleanArraySerializer.class).verify();
	}
}
