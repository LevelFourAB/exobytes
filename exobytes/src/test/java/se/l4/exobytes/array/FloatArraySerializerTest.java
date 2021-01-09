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

public class FloatArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<float[]> serializer = new FloatArraySerializer();

		StreamingInput in = write(out -> serializer.write(new float[] { 4f, 10f }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readFloat(), is(4f));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readFloat(), is(10f));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<float[]> serializer = new FloatArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeFloat(4f);
			out.writeFloat(10f);
			out.writeListEnd();
		})
			.get();

		float[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new float[] { 4f, 10f }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<float[]> serializer = new FloatArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeFloat(4f);
			out.writeFloat(10f);
			out.writeListEnd();
		})
			.get();

		float[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new float[] { 4f, 10f }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(FloatArraySerializer.class).verify();
	}
}
