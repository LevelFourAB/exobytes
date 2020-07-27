package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class DoubleFieldProperty
	extends FieldProperty
{
	public DoubleFieldProperty(
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
			return 0.0;
		}

		in.next(Token.VALUE);
		return in.readDouble();
	}

	@Override
	public void read(StreamingInput in, Object obj)
		throws IOException
	{
		double value;
		if(in.peek() == Token.NULL)
		{
			in.next();
			value = 0.0;
		}
		else
		{
			in.next(Token.VALUE);
			value = in.readDouble();
		}

		try
		{
			field.setDouble(obj, value);
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
		double value;

		try
		{
			value = field.getDouble(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == 0.0)
		{
			return;
		}

		out.writeString(name);
		out.writeDouble(value);
	}
}
