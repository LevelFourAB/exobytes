package se.l4.exobytes.standard;

import java.io.IOException;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer for {@link Number}, {@link Boolean} or {@link String}.
 */
public final class SimpleTypeSerializer
	implements Serializer<Object>
{
	@Override
	public Object read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readDynamic();
	}

	@Override
	public void write(Object object, StreamingOutput stream)
		throws IOException
	{
		if(object instanceof Byte)
		{
			stream.writeInt(((Byte) object).intValue());
		}
		else if(object instanceof Integer)
		{
			stream.writeInt((Integer) object);
		}
		else if(object instanceof Long)
		{
			stream.writeLong((Long) object);
		}
		else if(object instanceof Float)
		{
			stream.writeFloat((Float) object);
		}
		else if(object instanceof Double)
		{
			stream.writeDouble((Double) object);
		}
		else if(object instanceof Boolean)
		{
			stream.writeBoolean((Boolean) object);
		}
		else if(object instanceof String)
		{
			stream.writeString((String) object);
		}
		else
		{
			throw new SerializationException("Can't serialize the given object: " + object);
		}
	}

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && (this == obj || getClass() == obj.getClass());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{}";
	}
}
