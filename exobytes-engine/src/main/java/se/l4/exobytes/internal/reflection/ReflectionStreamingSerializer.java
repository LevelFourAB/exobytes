package se.l4.exobytes.internal.reflection;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerFormatDefinition;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

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
		while(in.peek() != Token.OBJECT_END)
		{
			in.next(Token.KEY);
			String key = in.readString();

			FieldDefinition def = type.getField(key);
			if(def == null)
			{
				// No such field, skip the entire value
				in.skipValue();
			}
			else
			{
				def.read(instance, in);
			}
		}

		in.next(Token.OBJECT_END);
		return instance;
	}

	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart();

		for(FieldDefinition def : type.getAllFields())
		{
			def.write(object, stream);
		}

		stream.writeObjectEnd();
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return type.getFormatDefinition();
	}
}
