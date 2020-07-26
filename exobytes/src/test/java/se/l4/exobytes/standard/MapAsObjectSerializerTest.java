package se.l4.exobytes.standard;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import se.l4.exobytes.SerializationTestHelper;
import se.l4.exobytes.SerializerTest;
import se.l4.exobytes.collections.MapAsObjectSerializer;
import se.l4.exobytes.collections.MapSerializerResolver;
import se.l4.exobytes.collections.StringKey;

/**
 * Tests for {@link MapAsObjectSerializer} that is resolved from
 * {@link MapSerializerResolver} if the annotation hint {@link StringKey}
 * is present.
 */
public class MapAsObjectSerializerTest
	extends SerializerTest
{
	@Test
	public void testEmptyMapWithStrings()
	{
		MapAsObjectSerializer<String> serializer = new MapAsObjectSerializer<>(new StringSerializer());
		Map<String, String> map = new HashMap<>();
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}

	@Test
	public void testMapWithStrings()
	{
		MapAsObjectSerializer<String> serializer = new MapAsObjectSerializer<>(new StringSerializer());
		Map<String, String> map = new HashMap<>();
		map.put("hello", "cookie");
		map.put("yum", null);
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}

	@Test
	public void testMapWithDynamicSerializer()
	{
		MapAsObjectSerializer<Object> serializer = new MapAsObjectSerializer<>(new DynamicSerializer.Impl(serializers));
		Map<String, Object> map = new HashMap<>();

		map.put("hello", "cookie");
		map.put("world", 129l);
		map.put("yum", null);
		SerializationTestHelper.testWriteAndRead(serializer, map);
	}
}
