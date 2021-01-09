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

public class IntArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<int[]> serializer = new IntArraySerializer();

		StreamingInput in = write(out -> serializer.write(new int[] { 10, -20 }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(10));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(-20));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<int[]> serializer = new IntArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeInt(10);
			out.writeInt(-20);
			out.writeListEnd();
		})
			.get();

		int[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new int[] { 10, -20 }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<int[]> serializer = new IntArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeInt(10);
			out.writeInt(-20);
			out.writeListEnd();
		})
			.get();

		int[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new int[] { 10, -20 }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(IntArraySerializer.class).verify();
	}
}
