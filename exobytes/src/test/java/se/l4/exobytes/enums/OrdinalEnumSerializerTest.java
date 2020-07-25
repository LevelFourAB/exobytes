package se.l4.exobytes.enums;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class OrdinalEnumSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<NonAnnotatedEnum> serializer = new EnumOrdinalSerializer.Impl<>(NonAnnotatedEnum.class);

		StreamingInput in = write(out -> serializer.write(NonAnnotatedEnum.N2, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readInt(), is(1));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<NonAnnotatedEnum> serializer = new EnumOrdinalSerializer.Impl<>(NonAnnotatedEnum.class);

		StreamingInput in = write(out -> out.writeInt(1))
			.get();

		NonAnnotatedEnum v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(NonAnnotatedEnum.N2));
	}

	@Test
	public void testAnnotatedEnumResolvesCorrectly()
	{
		Serializer<AnnotatedEnum> serializer = serializers.get(AnnotatedEnum.class);
		assertThat(serializer, instanceOf(EnumOrdinalSerializer.Impl.class));
	}

	public enum NonAnnotatedEnum
	{
		N1,
		N2
	}

	@Ordinal
	public enum AnnotatedEnum
	{
		A1,
		A2
	}
}
