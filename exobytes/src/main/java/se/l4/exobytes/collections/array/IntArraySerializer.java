package se.l4.exobytes.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.collections.ArraySerializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Custom serializer for arrays of integers.
 */
public class IntArraySerializer
	implements Serializer<int[]>
{
	@Override
	public int[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		int length = 0;
		int[] current = new int[512];
		while(in.peek() != Token.LIST_END)
		{
			in.next(Token.VALUE);

			if(length == current.length)
			{
				int newSize = ArraySerializer.growArray(current.length);
				current = Arrays.copyOf(current, newSize);
			}

			current[length++] = in.readInt();
		}

		in.next(Token.LIST_END);
		return Arrays.copyOf(current, length);
	}

	@Override
	public void write(int[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(int v : object)
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
