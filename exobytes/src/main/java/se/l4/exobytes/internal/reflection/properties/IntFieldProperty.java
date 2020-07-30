package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Property for a {@link Field} that contains a {@code int}.
 */
public class IntFieldProperty
	extends FieldProperty
{
	public IntFieldProperty(
		String name,
		Field field,
		boolean skipIfDefault
	)
	{
		super(name, field, skipIfDefault);
	}

	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		if(in.next() == Token.NULL)
		{
			return 0;
		}

		return in.readInt();
	}

	@Override
	public void readAndSet(StreamingInput in, Object obj)
		throws IOException
	{
		int value;
		if(in.next() == Token.NULL)
		{
			value = 0;
		}
		else
		{
			value = in.readInt();
		}

		try
		{
			field.setInt(obj, value);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to read; " + e.getMessage(), e);
		}
	}

	@Override
	public void write(Object obj, StreamingOutput out)
		throws IOException
	{
		int value;

		try
		{
			value = field.getInt(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == 0)
		{
			return;
		}

		out.writeString(name);
		out.writeInt(value);
	}
}
