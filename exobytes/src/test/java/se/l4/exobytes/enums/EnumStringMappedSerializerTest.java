package se.l4.exobytes.enums;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

public class EnumStringMappedSerializerTest
	extends SerializerTest
{
	@Test
	public void testWrite()
		throws IOException
	{
		Serializer<NonAnnotatedEnum> serializer = EnumStringMappedSerializer.create(NonAnnotatedEnum.class)
			.add(NonAnnotatedEnum.N1, "n-1")
			.add(NonAnnotatedEnum.N2, "n-2")
			.build();

		StreamingInput in = write(out -> serializer.write(NonAnnotatedEnum.N2, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("n-2"));
	}

	@Test
	public void testRead()
		throws IOException
	{
		Serializer<NonAnnotatedEnum> serializer = EnumStringMappedSerializer.create(NonAnnotatedEnum.class)
			.add(NonAnnotatedEnum.N1, "n-1")
			.add(NonAnnotatedEnum.N2, "n-2")
			.build();

		StreamingInput in = write(out -> out.writeString("n-2"))
			.get();

		NonAnnotatedEnum v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(NonAnnotatedEnum.N2));
	}

	@Test
	public void testResolvedSerializerWrite()
		throws IOException
	{
		Serializer<AnnotatedEnum> serializer = serializers.get(AnnotatedEnum.class);
		assertThat(serializer, instanceOf(EnumStringMappedSerializer.Impl.class));

		StreamingInput in = write(out -> serializer.write(AnnotatedEnum.A2, out))
			.get();

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("a-2"));
	}

	@Test
	public void testResolvedSerializerRead()
		throws IOException
	{
		Serializer<AnnotatedEnum> serializer = serializers.get(AnnotatedEnum.class);
		assertThat(serializer, instanceOf(EnumStringMappedSerializer.Impl.class));

		StreamingInput in = write(out -> out.writeString("a-1"))
			.get();

		AnnotatedEnum v = serializer.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat(v, is(AnnotatedEnum.A1));
	}


	public enum NonAnnotatedEnum
	{
		N1,
		N2
	}

	@StringMapped
	public enum AnnotatedEnum
	{
		@StringMapped.Value("a-1")
		A1,

		@StringMapped.Value("a-2")
		A2
	}
}
