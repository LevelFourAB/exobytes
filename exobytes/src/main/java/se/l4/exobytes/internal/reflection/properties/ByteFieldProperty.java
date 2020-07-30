package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Property for a {@link Field} that contains a {@code byte}.
 */
public class ByteFieldProperty
	extends FieldProperty
{
	public ByteFieldProperty(
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
			return (byte) 0;
		}

		return in.readByte();
	}

	@Override
	public void readAndSet(StreamingInput in, Object obj)
		throws IOException
	{
		byte value;
		if(in.next() == Token.NULL)
		{
			value = 0;
		}
		else
		{
			value = in.readByte();
		}

		try
		{
			field.setByte(obj, value);
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
		byte value;

		try
		{
			value = field.getByte(obj);
		}
		catch(IllegalArgumentException | IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}

		if(skipIfDefault && value == (byte) 0)
		{
			return;
		}

		out.writeString(name);
		out.writeByte(value);
	}
}
