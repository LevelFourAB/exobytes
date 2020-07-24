package se.l4.exobytes;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import se.l4.commons.io.Bytes;
import se.l4.exobytes.collections.ListSerializerResolver;
import se.l4.exobytes.collections.MapSerializerResolver;
import se.l4.exobytes.collections.SetSerializerResolver;
import se.l4.exobytes.enums.EnumSerializerResolver;
import se.l4.exobytes.standard.BooleanSerializer;
import se.l4.exobytes.standard.ByteArraySerializer;
import se.l4.exobytes.standard.ByteSerializer;
import se.l4.exobytes.standard.BytesSerializer;
import se.l4.exobytes.standard.CharacterSerializer;
import se.l4.exobytes.standard.DoubleSerializer;
import se.l4.exobytes.standard.FloatSerializer;
import se.l4.exobytes.standard.IntSerializer;
import se.l4.exobytes.standard.LongSerializer;
import se.l4.exobytes.standard.OptionalSerializerResolver;
import se.l4.exobytes.standard.ShortSerializer;
import se.l4.exobytes.standard.StringSerializer;
import se.l4.exobytes.standard.UuidSerializer;
import se.l4.commons.types.DefaultInstanceFactory;
import se.l4.commons.types.InstanceFactory;

/**
 * Default implementation of {@link Serializers}.
 */
public class DefaultSerializers
	extends AbstractSerializers
{
	private final InstanceFactory instanceFactory;

	public DefaultSerializers()
	{
		this(new DefaultInstanceFactory());
	}

	public DefaultSerializers(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;

		// Standard types
		bind(Boolean.class, new BooleanSerializer());
		bind(Byte.class, new ByteSerializer());
		bind(Character.class, new CharacterSerializer());
		bind(Double.class, new DoubleSerializer());
		bind(Float.class, new FloatSerializer());
		bind(Integer.class, new IntSerializer());
		bind(Long.class, new LongSerializer());
		bind(Short.class, new ShortSerializer());
		bind(String.class, new StringSerializer());
		bind(byte[].class, new ByteArraySerializer());
		bind(UUID.class, new UuidSerializer());

		// Collections
		bind(List.class, new ListSerializerResolver());
		bind(Map.class, new MapSerializerResolver());
		bind(Set.class, new SetSerializerResolver());

		// Enums
		bind(Enum.class, new EnumSerializerResolver());

		// Optional<T>
		bind(Optional.class, new OptionalSerializerResolver());

		bind(Bytes.class, new BytesSerializer());
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}
}
