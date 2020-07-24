package se.l4.exobytes.enums;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import org.eclipse.collections.api.map.primitive.IntObjectMap;
import org.eclipse.collections.api.map.primitive.MutableIntObjectMap;
import org.eclipse.collections.impl.factory.primitive.IntBags;
import org.eclipse.collections.impl.factory.primitive.IntObjectMaps;

import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Resolver that will resolve a serializer for an {@link Enum} that has been
 * annotated with {@link IntMapped}.
 */
public class EnumIntMappedSerializer
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
			IntMapped.Value value = type.getField(constant.name())
				.flatMap(c -> c.getAnnotation(IntMapped.Value.class))
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
		private final int[] values;

		Builder(Class<E> type)
		{
			constants = type.getEnumConstants();
			values = new int[constants.length];
		}

		/**
		 * Add a new mapping for this enum.
		 *
		 * @param value
		 * @param mapping
		 * @return
		 */
		public Builder<E> add(E value, int mapping)
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
			if(! IntBags.immutable.of(values)
				.selectDuplicates()
				.isEmpty())
			{
				// There are some duplicate values, abort
				throw new SerializationException("Multiple enum values can not be mapped to the same integer");
			}

			MutableIntObjectMap<E> mappedToEnum = IntObjectMaps.mutable.empty();
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
		private final IntObjectMap<E> mappedToEnum;
		private final int[] enumToMapped;

		Impl(
			int[] enumToMapped,
			IntObjectMap<E> mappedToEnum
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

			int v = in.readInt();
			return mappedToEnum.get(v);
		}

		@Override
		public void write(E object, StreamingOutput out)
			throws IOException
		{
			out.writeInt(enumToMapped[object.ordinal()]);
		}
	}
}
