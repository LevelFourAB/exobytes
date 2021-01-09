package se.l4.exobytes.array;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer for arrays.
 */
public class ArraySerializer
	implements Serializer<Object>
{
	private final Class<?> componentType;
	@SuppressWarnings("rawtypes")
	private final Serializer itemSerializer;

	public ArraySerializer(Class<?> componentType, Serializer<?> itemSerializer)
	{
		this.componentType = componentType;
		this.itemSerializer = itemSerializer;
	}

	@Override
	@SuppressWarnings({ "unchecked" })
	public Object read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		List<Object> list = new ArrayList<>();
		while(in.peek() != Token.LIST_END)
		{
			list.add(in.readObject(itemSerializer));
		}

		in.next(Token.LIST_END);

		Object array = Array.newInstance(componentType, list.size());
		for(int i=0, n=list.size(); i<n; i++)
		{
			Array.set(array, i, list.get(i));
		}
		return array;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void write(Object object, StreamingOutput stream)
		throws IOException
	{
		int n = Array.getLength(object);
		stream.writeListStart(n);
		for(int i=0; i<n; i++)
		{
			Object value = Array.get(object, i);
			stream.writeObject(itemSerializer, value);
		}
		stream.writeListEnd();
	}

	/**
	 * Helper for calculating the size of the array when it needs to grow.
	 *
	 * @param currentSize
	 *   the current size of the array
	 */
	public static int growArray(int currentSize)
	{
		return currentSize + (currentSize >> 1);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(componentType, itemSerializer);
	}

	@Override
	public boolean equals(Object obj)
	{
		if(this == obj) return true;
		if(obj == null) return false;
		if(getClass() != obj.getClass()) return false;
		ArraySerializer other = (ArraySerializer) obj;
		return Objects.equals(componentType, other.componentType)
			&& Objects.equals(itemSerializer, other.itemSerializer);
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{componentType=" + componentType + ", itemSerializer=" + itemSerializer + "}";
	}
}
