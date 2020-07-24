package se.l4.exobytes.enums;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Resolver that resolves a {@link Serializer} for {@link Enum} that uses
 * {@link Enum#name()}.
 */
public class EnumIgnoreCaseNameSerializer
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
	 * {@link #name()} while being case insensitive in reading.
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

			String name = in.readString();
			for(E e : values)
			{
				if(name.equalsIgnoreCase(e.name()))
				{
					return e;
				}
			}

			return null;
		}

		@Override
		public void write(E object, StreamingOutput out)
			throws IOException
		{
			out.writeString(object.name());
		}
	}
}
