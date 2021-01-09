package se.l4.exobytes.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Custom serializer for arrays of longs.
 */
public final class LongArraySerializer
	implements Serializer<long[]>
{
	@Override
	public long[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		long[] result;
		if(in.getLength().isPresent())
		{
			result = new long[in.getLength().getAsInt()];
			int length = 0;
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);
				result[length++] = in.readLong();
			}
		}
		else
		{
			int length = 0;
			long[] current = new long[512];
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);

				if(length == current.length)
				{
					int newSize = ArraySerializer.growArray(current.length);
					current = Arrays.copyOf(current, newSize);
				}

				current[length++] = in.readLong();
			}

			result = Arrays.copyOf(current, length);
		}

		in.next(Token.LIST_END);
		return result;
	}

	@Override
	public void write(long[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(long v : object)
		{
			out.writeLong(v);
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
