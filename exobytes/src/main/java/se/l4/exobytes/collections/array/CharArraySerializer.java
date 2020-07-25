package se.l4.exobytes.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.collections.ArraySerializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Custom serializer for arrays of chars.
 */
public final class CharArraySerializer
	implements Serializer<char[]>
{
	@Override
	public char[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		char[] result;
		if(in.getLength().isPresent())
		{
			result = new char[in.getLength().getAsInt()];
			int length = 0;
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);
				result[length++] = in.readChar();
			}
		}
		else
		{
			int length = 0;
			char[] current = new char[512];
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);

				if(length == current.length)
				{
					int newSize = ArraySerializer.growArray(current.length);
					current = Arrays.copyOf(current, newSize);
				}

				current[length++] = in.readChar();
			}

			result = Arrays.copyOf(current, length);
		}

		in.next(Token.LIST_END);
		return result;
	}

	@Override
	public void write(char[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(char v : object)
		{
			out.writeChar(v);
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
