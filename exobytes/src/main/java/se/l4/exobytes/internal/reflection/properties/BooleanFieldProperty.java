package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class BooleanFieldProperty
	extends FieldProperty
{
	public BooleanFieldProperty(
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
			in.next(Token.NULL);
			return false;
		}

		in.next(Token.VALUE);
		return in.readBoolean();
	}

	@Override
	public void read(StreamingInput in, Object obj)
		throws IOException
	{
		boolean value;
		if(in.peek() == Token.NULL)
		{
			in.next(Token.NULL);
			value = false;
		}
		else
		{
			in.next(Token.VALUE);
			value = in.readBoolean();
		}

		try
		{
			field.setBoolean(obj, value);
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
		boolean value;

		try
		{
			value = field.getBoolean(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == false)
		{
			return;
		}

		out.writeString(name);
		out.writeBoolean(value);
	}
}
