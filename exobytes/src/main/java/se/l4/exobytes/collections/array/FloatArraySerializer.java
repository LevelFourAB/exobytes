package se.l4.exobytes.collections.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.collections.ArraySerializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Custom serializer for arrays of floats.
 */
public class FloatArraySerializer
	implements Serializer<float[]>
{

	@Override
	public float[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		int length = 0;
		float[] current = new float[512];
		while(in.peek() != Token.LIST_END)
		{
			in.next(Token.VALUE);

			if(length == current.length)
			{
				int newSize = ArraySerializer.growArray(current.length);
				current = Arrays.copyOf(current, newSize);
			}

			current[length++] = in.readFloat();
		}

		in.next(Token.LIST_END);
		return Arrays.copyOf(current, length);
	}

	@Override
	public void write(float[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(float v : object)
		{
			out.writeFloat(v);
		}
		out.writeListEnd();
	}

}
