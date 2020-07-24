package se.l4.exobytes.collections;

import java.util.Optional;

import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.collections.array.BooleanArraySerializer;
import se.l4.exobytes.collections.array.CharArraySerializer;
import se.l4.exobytes.collections.array.DoubleArraySerializer;
import se.l4.exobytes.collections.array.FloatArraySerializer;
import se.l4.exobytes.collections.array.IntArraySerializer;
import se.l4.exobytes.collections.array.LongArraySerializer;
import se.l4.exobytes.collections.array.ShortArraySerializer;

/**
 * Resolver for array types.
 */
@SuppressWarnings("rawtypes")
public class ArraySerializerResolver
	implements SerializerResolver
{

	@Override
	public Optional<Serializer<?>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().getComponentType().isPresent())
		{
			return Optional.empty();
		}

		TypeRef componentType = encounter.getType().getComponentType()
			.get();

		/*
		 * Resolve the serializer by first looking for serializers that handle
		 * primitive types. If the component type of the array is not primitive
		 * we fallback on a serializer that can handle objects.
		 */

		if(componentType.isErasedType(char.class))
		{
			return Optional.of(new CharArraySerializer());
		}
		else if(componentType.isErasedType(boolean.class))
		{
			return Optional.of(new BooleanArraySerializer());
		}
		else if(componentType.isErasedType(double.class))
		{
			return Optional.of(new DoubleArraySerializer());
		}
		else if(componentType.isErasedType(float.class))
		{
			return Optional.of(new FloatArraySerializer());
		}
		else if(componentType.isErasedType(int.class))
		{
			return Optional.of(new IntArraySerializer());
		}
		else if(componentType.isErasedType(long.class))
		{
			return Optional.of(new LongArraySerializer());
		}
		else if(componentType.isErasedType(short.class))
		{
			return Optional.of(new ShortArraySerializer());
		}

		Serializer<?> itemSerializer = encounter.getCollection().find(componentType);
		return Optional.of(new ArraySerializer(componentType.getErasedType(), itemSerializer));
	}
}
