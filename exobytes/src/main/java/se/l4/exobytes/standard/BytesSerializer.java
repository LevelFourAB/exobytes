package se.l4.exobytes.standard;

import java.io.IOException;

import se.l4.commons.io.Bytes;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer for {@link Bytes}.
 */
public final class BytesSerializer
	implements Serializer<Bytes>
{
	@Override
	public Bytes read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return Bytes.create(in.readByteArray());
	}

	@Override
	public void write(Bytes object, StreamingOutput out)
		throws IOException
	{
		out.writeByteArray(object.toByteArray());
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
