package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Resolver that resolves a suitable {@link OptionalSerializer} based on
 * the type declared.
 */
public class OptionalSerializer
	implements SerializerResolver<Optional<?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<Optional<?>>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.ofNullable(new Impl(encounter.get(type)));
	}

	public static <T> Serializer<Optional<T>> create(Serializer<T> itemSerializer)
	{
		return new Impl<>(itemSerializer);
	}

	/**
	 * Serializer for {@link Optional} values. Will treat null as an empty
	 * optional.
	 */
	static final class Impl<T>
		implements Serializer<Optional<T>>, Serializer.NullHandling
	{
		private final Serializer<T> itemSerializer;

		public Impl(Serializer<T> itemSerializer)
		{
			this.itemSerializer = itemSerializer;
		}

		@Override
		public Optional<T> read(StreamingInput in)
			throws IOException
		{
			if(in.peek() == Token.NULL)
			{
				// Consume the null value
				in.next();
				return Optional.empty();
			}

			T item = itemSerializer.read(in);
			return Optional.of(item);
		}

		@Override
		public void write(Optional<T> object, StreamingOutput stream)
			throws IOException
		{
			if(object != null && object.isPresent())
			{
				// Use the item serializer to serialize if there is an object present
				itemSerializer.write(object.get(), stream);
			}
			else
			{
				// If there is no object, write a null value
				stream.writeNull();
			}
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(itemSerializer);
		}

		@Override
		@SuppressWarnings({ "rawtypes" })
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			Impl other = (Impl) obj;
			return Objects.equals(itemSerializer, other.itemSerializer);
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{}";
		}
	}
}
