package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.util.Optional;

import org.junit.Test;

import nl.jqno.equalsverifier.EqualsVerifier;
import se.l4.commons.types.Types;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.Token;

public class OptionalSerializerTest
	extends SerializerTest
{
	@Test
	public void testDirectEmpty()
		throws IOException
	{
		Serializer<Optional<String>> s = OptionalSerializer.create(new StringSerializer());

		StreamingInput in = write(out -> out.writeObject(s, Optional.empty()))
			.get();

		Optional<String> opt = s.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectNull()
		throws IOException
	{
		Serializer<Optional<String>> s = OptionalSerializer.create(new StringSerializer());

		StreamingInput in = write(out -> out.writeObject(s, null))
			.get();

		Optional<String> opt = s.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is empty", opt.isPresent(), is(false));
	}

	@Test
	public void testDirectWithValue()
		throws IOException
	{
		Serializer<Optional<String>> s = OptionalSerializer.create(new StringSerializer());

		StreamingInput in = write(out -> out.writeObject(s, Optional.of("Hello")))
			.get();

		Optional<String> opt = s.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}

	@Test
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void testViaCollection()
		throws IOException
	{
		Serializer<Optional<String>> s = (Serializer) serializers.find(
			Types.reference(Optional.class, String.class)
		);

		StreamingInput in = write(out -> out.writeObject(s, Optional.of("Hello")))
			.get();

		Optional<String> opt = s.read(in);
		assertThat(in.next(), is(Token.END_OF_STREAM));

		assertThat("optional not null", opt, notNullValue());
		assertThat("optional is present", opt.isPresent(), is(true));
		assertThat("optional is Hello", opt.get(), is("Hello"));
	}

	@Test
	public void testEquality()
	{
		EqualsVerifier.forClass(OptionalSerializer.Impl.class).verify();
	}
}
