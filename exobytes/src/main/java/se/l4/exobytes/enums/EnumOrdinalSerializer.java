package se.l4.exobytes.enums;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Resolver that resolves a {@link Serializer} for {@link Enum} that uses
 * {@link Enum#ordinal()}.
 */
public class EnumOrdinalSerializer
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

		Serializer serializer = new Impl(encounter.getType().getErasedType());
		return (Optional) Optional.of(serializer);
	}

	/**
	 * Create a serializer that will serialize the given enum type using its
	 * ordinal values.
	 *
	 * @param <E>
	 * @param type
	 * @return
	 */
	public static <E extends Enum<E>> Serializer<E> create(Class<E> type)
	{
		return new Impl<>(type);
	}

	static class Impl<E extends Enum<E>>
		implements Serializer<E>
	{
		private final E[] values;

		public Impl(Class<E> type)
		{
			this.values = type.getEnumConstants();
		}

		@Override
		public E read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);

			int v = in.readInt();
			if(v >= 0 && v < values.length)
			{
				return values[v];
			}

			return null;
		}

		@Override
		public void write(E object, StreamingOutput out)
			throws IOException
		{
			out.writeInt(object.ordinal());
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(values);
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
			return Arrays.equals(values, other.values);
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{values=" + Arrays.toString(values) + "}";
		}
	}
}
