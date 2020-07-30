package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Property for a {@link Field} that works using a {@link Serializer}.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class ObjectFieldProperty
	extends FieldProperty
{
	protected final Field field;
	protected final Serializer serializer;
	protected final boolean nullHandling;

	public ObjectFieldProperty(
		String name,
		Field field,
		Serializer serializer,
		boolean skipIfDefault
	)
	{
		super(name, field, skipIfDefault);

		this.field = field;
		this.serializer = serializer;

		this.nullHandling = serializer instanceof Serializer.NullHandling;
	}

	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		if(in.peek() == Token.NULL)
		{
			if(nullHandling)
			{
				// Let the serializer handle the null value
				return serializer.read(in);
			}

			// Consume and return null
			in.next();
			return null;
		}

		return serializer.read(in);
	}

	@Override
	public void readAndSet(StreamingInput in, Object obj)
		throws IOException
	{
		set(obj, read(in));
	}

	@Override
	public void set(Object obj, Object value)
		throws IOException
	{
		try
		{
			field.set(obj, value);
		}
		catch(Exception e)
		{
			throw new SerializationException("Unable to read object; " + e.getMessage(), e);
		}
	}

	@Override
	public void write(Object obj, StreamingOutput out)
		throws IOException
	{
		Object value = get(obj);

		if(skipIfDefault && value == null)
		{
			// Write nothing as the default value and our value matches
			return;
		}

		out.writeString(name);

		if(value == null)
		{
			if(nullHandling)
			{
				serializer.write(null, out);
			}
			else
			{
				out.writeNull();
			}
		}
		else
		{
			serializer.write(value, out);
		}
	}
}
