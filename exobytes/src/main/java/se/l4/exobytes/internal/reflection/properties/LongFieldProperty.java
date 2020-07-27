package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class LongFieldProperty
	extends FieldProperty
{
	public LongFieldProperty(
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
			return 0l;
		}

		in.next(Token.VALUE);
		return in.readLong();
	}

	@Override
	public void read(StreamingInput in, Object obj)
		throws IOException
	{
		long value;
		if(in.peek() == Token.NULL)
		{
			in.next();
			value = 0;
		}
		else
		{
			in.next(Token.VALUE);
			value = in.readLong();
		}

		try
		{
			field.setLong(obj, value);
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
		long value;

		try
		{
			value = field.getLong(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == 0l)
		{
			return;
		}

		out.writeString(name);
		out.writeLong(value);
	}
}
