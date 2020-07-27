package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class FloatFieldProperty
	extends FieldProperty
{
	public FloatFieldProperty(
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
		if(in.peek() == Token.NULL)
		{
			return 0.0f;
		}

		in.next(Token.VALUE);
		return in.readFloat();
	}

	@Override
	public void read(StreamingInput in, Object obj)
		throws IOException
	{
		float value;
		if(in.peek() == Token.NULL)
		{
			in.next();
			value = 0.0f;
		}
		else
		{
			in.next(Token.VALUE);
			value = in.readFloat();
		}

		try
		{
			field.setFloat(obj, value);
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
		float value;

		try
		{
			value = field.getFloat(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == 0f)
		{
			return;
		}

		out.writeString(name);
		out.writeFloat(value);
	}
}
