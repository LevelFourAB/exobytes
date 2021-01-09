package se.l4.exobytes.collections;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Optional;
import java.util.function.IntFunction;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;
import se.l4.ylem.types.reflect.TypeRef;
import se.l4.ylem.types.reflect.Types;

/**
 * Resolver for {@link Map}.
 */
public class MapResolver<C extends Map<?, ?>>
	implements SerializerResolver<C>
{
	private final Class<C> type;
	private final IntFunction<? extends C> supplier;

	public MapResolver(
		Class<C> type,
		IntFunction<? extends C> supplier
	)
	{
		this.type = type;
		this.supplier = supplier;
	}

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<C>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(type))
		{
			return Optional.empty();
		}

		TypeRef keyType = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		TypeRef valueType = encounter.getType()
			.getTypeParameter(1)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.of(new AsObjectImpl(
			encounter.get(keyType),
			encounter.get(valueType),
			supplier
		));
	}

	static class AsObjectImpl<K, V, C extends Map<K, V>>
		implements Serializer<C>
	{
		private final Serializer<K> keySerializer;
		private final Serializer<V> valueSerializer;
		private final IntFunction<C> supplier;

		public AsObjectImpl(
			Serializer<K> keySerializer,
			Serializer<V> valueSerializer,
			IntFunction<C> supplier
		)
		{
			this.keySerializer = keySerializer;
			this.valueSerializer = valueSerializer;
			this.supplier = supplier;
		}

		@Override
		public C read(StreamingInput in)
			throws IOException
		{
			in.next(Token.OBJECT_START);

			C result = supplier.apply(in.getLength().orElse(16));
			while(in.peek() != Token.OBJECT_END)
			{
				if(in.peek() == Token.NULL)
				{
					// Due to old error with writing of null values
					in.next();
					continue;
				}

				K key = in.readObject(keySerializer);
				V value = in.readObject(valueSerializer);
				result.put(key, value);
			}

			in.next(Token.OBJECT_END);

			return result;
		}

		@Override
		public void write(C object, StreamingOutput stream)
			throws IOException
		{
			stream.writeObjectStart(object.size());

			for(Entry<K, V> e : object.entrySet())
			{
				stream.writeObject(keySerializer, e.getKey());
				stream.writeObject(valueSerializer, e.getValue());
			}

			stream.writeObjectEnd();
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(keySerializer, supplier, valueSerializer);
		}

		@Override
		@SuppressWarnings("rawtypes")
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			AsObjectImpl other = (AsObjectImpl) obj;
			return Objects.equals(keySerializer, other.keySerializer)
				&& Objects.equals(supplier, other.supplier)
				&& Objects.equals(valueSerializer, other.valueSerializer);
		}

		@Override
		public String toString()
		{
			return "MapResolver.AsObject{"
				+ "keySerializer=" + keySerializer + ","
				+ "valueSerializer=" + valueSerializer + ","
				+ "supplier=" + supplier + "}";
		}
	}
}
