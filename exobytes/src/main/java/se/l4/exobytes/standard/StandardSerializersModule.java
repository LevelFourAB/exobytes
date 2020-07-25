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
		serializers.bind(Boolean.class, new BooleanSerializer());
		serializers.bind(Byte.class, new ByteSerializer());
		serializers.bind(Character.class, new CharacterSerializer());
		serializers.bind(Double.class, new DoubleSerializer());
		serializers.bind(Float.class, new FloatSerializer());
		serializers.bind(Integer.class, new IntSerializer());
		serializers.bind(Long.class, new LongSerializer());
		serializers.bind(Short.class, new ShortSerializer());
		serializers.bind(String.class, new StringSerializer());
		serializers.bind(byte[].class, new ByteArraySerializer());
		serializers.bind(UUID.class, new UuidSerializer());

		serializers.bind(Optional.class, new OptionalSerializer());
		serializers.bind(Bytes.class, new BytesSerializer());
	}
}
