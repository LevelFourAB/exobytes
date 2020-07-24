package se.l4.exobytes.collections;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link List}.
 *
 * @param <T>
 */
public class SetSerializer<T>
	implements Serializer<Set<T>>
{
	private final Serializer<T> itemSerializer;

	public SetSerializer(Serializer<T> itemSerializer)
	{
		this.itemSerializer = itemSerializer;
	}

	@Override
	public Set<T> read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		Set<T> list = new HashSet<T>();
		while(in.peek() != Token.LIST_END)
		{
			list.add(in.readObject(itemSerializer));
		}

		in.next(Token.LIST_END);

		return list;
	}

	@Override
	public void write(Set<T> object, StreamingOutput stream)
		throws IOException
	{
		stream.writeListStart(object.size());

		for(T value : object)
		{
			stream.writeObject(itemSerializer, value);
		}

		stream.writeListEnd();
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
		SetSerializer other = (SetSerializer) obj;
		return Objects.equals(itemSerializer, other.itemSerializer);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{itemSerializer=" + itemSerializer + "}";
	}
}
