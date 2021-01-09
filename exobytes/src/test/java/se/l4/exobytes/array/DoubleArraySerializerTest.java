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

public class DoubleArraySerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<double[]> serializer = new DoubleArraySerializer();

		StreamingInput in = write(out -> serializer.write(new double[] { 3.14, 10.0 }, out))
			.get();

		assertThat(in.next(), is(Token.LIST_START));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(3.14));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readDouble(), is(10.0));
		assertThat(in.next(), is(Token.LIST_END));
	}

	@Test
	public void testReadFixed()
		throws IOException
	{
		Serializer<double[]> serializer = new DoubleArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart(2);
			out.writeDouble(3.14);
			out.writeDouble(10);
			out.writeListEnd();
		})
			.get();

			double[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new double[] { 3.14, 10.0 }));
	}

	@Test
	public void testReadIndeterminate()
		throws IOException
	{
		Serializer<double[]> serializer = new DoubleArraySerializer();

		StreamingInput in = write(out -> {
			out.writeListStart();
			out.writeDouble(3.14);
			out.writeDouble(10);
			out.writeListEnd();
		})
			.get();

			double[] v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(new double[] { 3.14, 10.0 }));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(DoubleArraySerializer.class).verify();
	}
}
