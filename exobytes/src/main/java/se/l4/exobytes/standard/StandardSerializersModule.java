package se.l4.exobytes.standard;

import java.util.Optional;
import java.util.UUID;

import se.l4.exobytes.Serializers;
import se.l4.exobytes.SerializersModule;

/**
 * Module for the standard serializers for wrapped primitives and other common
 * Java types.
 */
public class StandardSerializersModule
	implements SerializersModule
{
	@Override
	public void activate(Serializers serializers)
	{
		serializers.register(Boolean.class, BooleanSerializer.INSTANCE);
		serializers.register(Byte.class, ByteSerializer.INSTANCE);
		serializers.register(Character.class, CharacterSerializer.INSTANCE);
		serializers.register(Double.class, DoubleSerializer.INSTANCE);
		serializers.register(Float.class, FloatSerializer.INSTANCE);
		serializers.register(Integer.class, IntSerializer.INSTANCE);
		serializers.register(Long.class, LongSerializer.INSTANCE);
		serializers.register(Short.class, ShortSerializer.INSTANCE);
		serializers.register(String.class, StringSerializer.INSTANCE);
		serializers.register(byte[].class, ByteArraySerializer.INSTANCE);
		serializers.register(UUID.class, UuidSerializer.INSTANCE);

		serializers.register(Optional.class, new OptionalSerializer());
	}
}
