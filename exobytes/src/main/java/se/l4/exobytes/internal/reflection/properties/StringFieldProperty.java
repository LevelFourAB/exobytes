package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class StringFieldProperty
	extends FieldProperty
{
	public StringFieldProperty(
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
			return null;
		}

		in.next(Token.VALUE);
		return in.readString();
	}

	@Override
	public void write(Object obj, StreamingOutput out)
		throws IOException
	{
		String value = (String) get(obj);
		if(skipIfDefault && value == null)
		{
			return;
		}

		out.writeString(name);
		out.writeString(value);
	}
}
