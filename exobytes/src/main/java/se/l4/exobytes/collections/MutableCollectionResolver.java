package se.l4.exobytes.collections;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.RandomAccess;
import java.util.function.IntFunction;

import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class MutableCollectionResolver<C extends Collection<?>>
	implements SerializerResolver<C>
{
	private final Class<C> type;
	private final IntFunction<? extends C> supplier;

	public MutableCollectionResolver(
		Class<C> type,
		IntFunction<? extends C> supplier
	)
	{
		this.type = type;
		this.supplier = supplier;
	}

	@Override
	public Optional<Serializer<C>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(type))
		{
			return Optional.empty();
		}

		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.of(new Impl(encounter.find(type), supplier));
	}

	/**
	 * Serializer for {@link List}.
	 *
	 * @param <T>
	 */
	static class Impl<T, C extends Collection<T>>
		implements Serializer<C>
	{
		private final Serializer<T> itemSerializer;
		private final IntFunction<C> supplier;

		public Impl(Serializer<T> itemSerializer, IntFunction<C> supplier)
		{
			this.itemSerializer = itemSerializer;
			this.supplier = supplier;
		}

		@Override
		public C read(StreamingInput in)
			throws IOException
		{
			in.next(Token.LIST_START);

			C list = supplier.apply(in.getLength().orElse(16));
			while(in.peek() != Token.LIST_END)
			{
				list.add(in.readObject(itemSerializer));
			}

			in.next(Token.LIST_END);

			return list;
		}

		@Override
		public void write(C object, StreamingOutput stream)
			throws IOException
		{
			stream.writeListStart(object.size());

			if(object instanceof RandomAccess && object instanceof List)
			{
				List<T> list = (List<T>) object;
				for(int i=0, n=list.size(); i<n; i++)
				{
					T value = list.get(i);
					stream.writeObject(itemSerializer, value);
				}
			}
			else
			{
				for(T value : object)
				{
					stream.writeObject(itemSerializer, value);
				}
			}

			stream.writeListEnd();
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(itemSerializer, supplier);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			Impl other = (Impl) obj;
			return Objects.equals(itemSerializer, other.itemSerializer)
				&& Objects.equals(supplier, other.supplier);
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{itemSerializer=" + itemSerializer + ", supplier=" + supplier + "}";
		}
	}
}
