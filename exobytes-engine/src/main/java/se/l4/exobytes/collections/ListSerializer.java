package se.l4.exobytes.collections;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.RandomAccess;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerFormatDefinition;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link List}.
 *
 * @param <T>
 */
public class ListSerializer<T>
	implements Serializer<List<T>>
{
	private final Serializer<T> itemSerializer;
	private final SerializerFormatDefinition formatDefinition;

	public ListSerializer(Serializer<T> itemSerializer)
	{
		this.itemSerializer = itemSerializer;

		formatDefinition = SerializerFormatDefinition.builder()
			.list(itemSerializer)
			.build();
	}

	@Override
	public List<T> read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		List<T> list = new ArrayList<T>();
		while(in.peek() != Token.LIST_END)
		{
			list.add(in.readObject(itemSerializer));
		}

		in.next(Token.LIST_END);

		return list;
	}

	@Override
	public void write(List<T> object, StreamingOutput stream)
		throws IOException
	{
		stream.writeListStart(object.size());

		if(object instanceof RandomAccess)
		{
			for(int i=0, n=object.size(); i<n; i++)
			{
				T value = object.get(i);
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
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
