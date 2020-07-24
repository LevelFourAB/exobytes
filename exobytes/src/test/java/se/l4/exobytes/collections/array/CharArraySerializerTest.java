package se.l4.exobytes.collections.array;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.Token;

public class CharArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<char[]> serializer = new CharArraySerializer();

		StreamingInput in = write(out -> serializer.write(new char[] { 'a', 'f' }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readChar(), is('a'));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readChar(), is('f'));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<char[]> serializer = new CharArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeChar('a');
			out.writeChar('f');
			out.writeListEnd();
		})
			.get();

		char[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new char[] { 'a', 'f' }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<char[]> serializer = new CharArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeChar('a');
			out.writeChar('f');
			out.writeListEnd();
		})
			.get();

		char[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new char[] { 'a', 'f' }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(CharArraySerializer.class).verify();
	}
}
