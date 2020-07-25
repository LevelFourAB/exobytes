package se.l4.exobytes.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.collections.ArraySerializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Custom serializer for arrays of shorts.
 */
public final class ShortArraySerializer
	implements Serializer<short[]>
{
	@Override
	public short[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		short[] result;
		if(in.getLength().isPresent())
		{
			result = new short[in.getLength().getAsInt()];
			int length = 0;
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);
				result[length++] = in.readShort();
			}
		}
		else
		{
			int length = 0;
			short[] current = new short[512];
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);

				if(length == current.length)
				{
					int newSize = ArraySerializer.growArray(current.length);
					current = Arrays.copyOf(current, newSize);
				}

				current[length++] = in.readShort();
			}

			result = Arrays.copyOf(current, length);
		}

		in.next(Token.LIST_END);
		return result;
	}

	@Override
	public void write(short[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(short v : object)
		{
			out.writeInt(v);
		}
		out.writeListEnd();
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
