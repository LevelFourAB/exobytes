package se.l4.exobytes.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.collections.ArraySerializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Custom serializer for arrays of shorts.
 */
public class ShortArraySerializer
	implements Serializer<short[]>
{

	@Override
	public short[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

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

		in.next(Token.LIST_END);
		return Arrays.copyOf(current, length);
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

}
