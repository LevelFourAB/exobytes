package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class CharFieldProperty
	extends FieldProperty
{
	public CharFieldProperty(
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
			return '\0';
		}

		in.next(Token.VALUE);
		return in.readChar();
	}

	@Override
	public void read(StreamingInput in, Object obj)
		throws IOException
	{
		char value;
		if(in.peek() == Token.NULL)
		{
			in.next(Token.NULL);
			value = '\0';
		}
		else
		{
			in.next(Token.VALUE);
			value = in.readChar();
		}

		try
		{
			field.setChar(obj, value);
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
		char value;

		try
		{
			value = field.getChar(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == (char) 0)
		{
			return;
		}

		out.writeString(name);
		out.writeChar(value);
	}
}
