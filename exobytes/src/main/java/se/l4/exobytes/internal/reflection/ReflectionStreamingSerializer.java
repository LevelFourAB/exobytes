package se.l4.exobytes.internal.reflection;

import java.io.IOException;
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
public class ReflectionStreamingSerializer<T>
	implements Serializer<T>
{
	private final TypeInfo<T> type;

	public ReflectionStreamingSerializer(TypeInfo<T> type)
	{
		this.type = type;
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

		T instance = type.newInstance(null);
		while(true)
		{
			if(in.next() == Token.OBJECT_END)
			{
				break;
			}

			in.current(Token.VALUE);
			String key = in.readString();

			SerializableProperty property = type.getProperty(key);
			if(property == null)
			{
				// No such field, skip the entire value
				in.skipNext();
			}
			else
			{
				property.readAndSet(in, instance);
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
