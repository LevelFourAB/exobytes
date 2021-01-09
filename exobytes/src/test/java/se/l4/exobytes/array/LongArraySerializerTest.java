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

public class LongArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<long[]> serializer = new LongArraySerializer();

		StreamingInput in = write(out -> serializer.write(new long[] { 10, -20 }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(10l));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readLong(), is(-20l));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<long[]> serializer = new LongArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeLong(10);
			out.writeLong(-20);
			out.writeListEnd();
		})
			.get();

		long[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new long[] { 10, -20 }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<long[]> serializer = new LongArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeLong(10);
			out.writeLong(-20);
			out.writeListEnd();
		})
			.get();

		long[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new long[] { 10, -20 }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(LongArraySerializer.class).verify();
	}
}
