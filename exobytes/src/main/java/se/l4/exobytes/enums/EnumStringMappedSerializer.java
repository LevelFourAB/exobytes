package se.l4.exobytes.enums;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.collections.api.factory.Bags;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.api.map.MutableMap;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;
import se.l4.ylem.types.reflect.TypeRef;

/**
 * Resolver that will resolve a serializer for an {@link Enum} that has been
 * annotated with {@link StringMapped}.
 */
public class EnumStringMappedSerializer
	implements SerializerResolver<Enum<?>>
{

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<? extends SerializerOrResolver<Enum<?>>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isEnum())
		{
			throw new SerializationException("This type of serializer can not be used with non-enums");
		}

		TypeRef type = encounter.getType();
		Class<? extends Enum> enumType = (Class<? extends Enum>) encounter.getType().getErasedType();
		Builder builder = create(enumType);

		for(Enum<?> constant : enumType.getEnumConstants())
		{
			StringMapped.Value value = type.getField(constant.name())
				.flatMap(c -> c.getAnnotation(StringMapped.Value.class))
				.orElseThrow(() -> new SerializationException("All enum values must be annotated with @IntMapped.Value"));

			builder.add(constant, value.value());
		}

		return (Optional) Optional.of(builder.build());
	}

	/**
	 * Start building a new integer mapped serializer for the given type of
	 * enum.
	 *
	 * @param <E>
	 * @param type
	 * @return
	 */
	public static <E extends Enum<E>> Builder<E> create(Class<E> type)
	{
		return new Builder<>(type);
	}

	public static class Builder<E extends Enum<E>>
	{
		private final E[] constants;
		private final String[] values;

		Builder(Class<E> type)
		{
			constants = type.getEnumConstants();
			values = new String[constants.length];
		}

		/**
		 * Add a new mapping for this enum.
		 *
		 * @param value
		 * @param mapping
		 * @return
		 */
		public Builder<E> add(E value, String mapping)
		{
			Objects.requireNonNull(value);
			values[value.ordinal()] = mapping;

			return this;
		}

		/**
		 * Build and return the serializer.
		 *
		 * @return
		 */
		public Serializer<E> build()
		{
			if(! Bags.immutable.of(values)
				.selectDuplicates()
				.isEmpty())
			{
				// There are some duplicate values, abort
				throw new SerializationException("Multiple enum values can not be mapped to the same string");
			}

			MutableMap<String, E> mappedToEnum = Maps.mutable.empty();
			for(int i=0, n=values.length; i<n; i++)
			{
				mappedToEnum.put(values[i], constants[i]);
			}

			return new Impl<>(values, mappedToEnum);
		}
	}

	static class Impl<E extends Enum<E>>
		implements Serializer<E>
	{
		private final MapIterable<String, E> mappedToEnum;
		private final String[] enumToMapped;

		Impl(
			String[] enumToMapped,
			MapIterable<String, E> mappedToEnum
		)
		{
			this.enumToMapped = enumToMapped;
			this.mappedToEnum = mappedToEnum;
		}

		@Override
		public E read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);

			String v = in.readString();
			return mappedToEnum.get(v);
		}

		@Override
		public void write(E object, StreamingOutput out)
			throws IOException
		{
			out.writeString(enumToMapped[object.ordinal()]);
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Objects.hashCode(mappedToEnum);
			return result;
		}

		@Override
		@SuppressWarnings({ "rawtypes" })
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			Impl other = (Impl) obj;
			return Objects.equals(mappedToEnum, other.mappedToEnum);
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{values=" + mappedToEnum + "}";
		}
	}
}
