package se.l4.exobytes.reflection;

import org.junit.jupiter.api.BeforeEach;

import se.l4.commons.types.Types;
import se.l4.commons.types.mapping.OutputDeduplicator;
import se.l4.exobytes.SerializationTestHelper;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.internal.TypeEncounterImpl;
import se.l4.exobytes.internal.reflection.ReflectionSerializer;

public class ReflectionTest
{
	protected Serializers collection;

	@BeforeEach
	public void beforeTests()
	{
		collection = Serializers.create()
			.build();
	}

	public <T> Serializer<T> resolve(Class<T> type)
	{
		return new ReflectionSerializer<T>()
			.find(new TypeEncounterImpl(collection, OutputDeduplicator.none(), Types.reference(type)))
			.get();
	}

	protected <T> void testSymmetry(Serializer<T> serializer, T instance)
	{
		SerializationTestHelper.testWriteAndRead(serializer, instance);
	}
}
