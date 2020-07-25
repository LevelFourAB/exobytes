package se.l4.exobytes.standard;

import java.util.Optional;
import java.util.UUID;

import se.l4.commons.io.Bytes;
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
		serializers.register(Boolean.class, new BooleanSerializer());
		serializers.register(Byte.class, new ByteSerializer());
		serializers.register(Character.class, new CharacterSerializer());
		serializers.register(Double.class, new DoubleSerializer());
		serializers.register(Float.class, new FloatSerializer());
		serializers.register(Integer.class, new IntSerializer());
		serializers.register(Long.class, new LongSerializer());
		serializers.register(Short.class, new ShortSerializer());
		serializers.register(String.class, new StringSerializer());
		serializers.register(byte[].class, new ByteArraySerializer());
		serializers.register(UUID.class, new UuidSerializer());

		serializers.register(Optional.class, new OptionalSerializer());
		serializers.register(Bytes.class, new BytesSerializer());
	}
}
