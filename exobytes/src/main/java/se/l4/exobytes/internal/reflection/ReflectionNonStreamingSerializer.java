package se.l4.exobytes.internal.reflection;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.internal.reflection.properties.SerializableProperty;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer that uses only fields or methods. Can fully stream the object.
 *
 * @param <T>
 */
public class ReflectionNonStreamingSerializer<T>
	implements Serializer<T>
{
	private final TypeInfo<T> type;
	private final int size;

	public ReflectionNonStreamingSerializer(TypeInfo<T> type)
	{
		this.type = type;
		this.size = type.getProperties().length;
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.ofNullable(type.getName());
	}

	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		in.next(Token.OBJECT_START);

		// First create a map with all the data
		Map<String, Object> data = new HashMap<>(size);
		while(in.peek() != Token.OBJECT_END)
		{
			in.next(Token.VALUE);
			String key = in.readString();

			SerializableProperty property = type.getProperty(key);
			if(property == null)
			{
				// No such field, skip the entire value
				in.skipNext();
			}
			else
			{
				data.put(key, property.read(in));
			}
		}

		in.next(Token.OBJECT_END);

		// Create the instance
		T instance = type.newInstance(data);

		// Transfer any other fields
		for(Map.Entry<String, Object> entry : data.entrySet())
		{
			SerializableProperty property = type.getProperty(entry.getKey());
			if(! property.isReadOnly())
			{
				property.set(instance, entry.getValue());
			}
		}

		return instance;
	}

	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart();

		for(SerializableProperty property : type.getProperties())
		{
			property.write(object, stream);
		}

		stream.writeObjectEnd();
	}
}
