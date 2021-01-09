package se.l4.exobytes.collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;
import se.l4.ylem.types.reflect.TypeUsage;
import se.l4.ylem.types.reflect.Types;

public class MapTest
	extends SerializerTest
{
	@SuppressWarnings({ "rawtypes", "unchecked" })
	protected <K, V> Serializer<Map<K, V>> serializer(
		Class<K> key,
		Class<V> value,
		Annotation... annotations
	)
	{
		return (Serializer) serializers.get(
			Types.reference(Map.class)
				.withTypeParameter(0, Types.reference(key)).get()
				.withTypeParameter(1, Types.reference(value)).get()
				.withUsage(TypeUsage.forAnnotations(annotations))
		);
	}

	@Test
	public void testAsObjectWriteEmpty()
		throws IOException
	{
		Serializer<Map<String, String>> serializer = serializer(
			String.class,
			String.class
		);

		Map<String, String> map = new HashMap<>();
		StreamingInput in = write(out -> serializer.write(map, out)).get();

		assertThat(in.next(), is(Token.OBJECT_START));
		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testAsObjectReadEmpty()
		throws IOException
	{
		Serializer<Map<String, String>> serializer = serializer(
			String.class,
			String.class
		);

		StreamingInput in = write(out -> {
			out.writeObjectStart();
			out.writeObjectEnd();
		}).get();

		Map<String, String> value = serializer.read(in);
		assertThat(value, is(new HashMap<>()));
	}

	@Test
	public void testAsObjectWrite()
		throws IOException
	{
		Serializer<Map<String, String>> serializer = serializer(
			String.class,
			String.class
		);

		Map<String, String> map = new HashMap<>();
		map.put("k1", "v1");
		StreamingInput in = write(out -> serializer.write(map, out)).get();

		assertThat(in.next(), is(Token.OBJECT_START));

		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("k1"));
		assertThat(in.next(), is(Token.VALUE));
		assertThat(in.readString(), is("v1"));

		assertThat(in.next(), is(Token.OBJECT_END));
		assertThat(in.next(), is(Token.END_OF_STREAM));
	}

	@Test
	public void testAsObjectRead()
		throws IOException
	{
		Serializer<Map<String, String>> serializer = serializer(
			String.class,
			String.class
		);

		StreamingInput in = write(out -> {
			out.writeObjectStart();
			out.writeString("k1");
			out.writeString("v1");
			out.writeObjectEnd();
		}).get();

		Map<String, String> value = serializer.read(in);
		Map<String, String> map = new HashMap<>();
		map.put("k1", "v1");

		assertThat(value, is(map));
	}
}
