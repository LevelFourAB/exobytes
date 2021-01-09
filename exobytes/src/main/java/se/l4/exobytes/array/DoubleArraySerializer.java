package se.l4.exobytes.array;

import java.io.IOException;
import java.util.Arrays;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Custom serializer for arrays of doubles.
 */
public final class DoubleArraySerializer
	implements Serializer<double[]>
{
	@Override
	public double[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.LIST_START);

		double[] result;
		if(in.getLength().isPresent())
		{
			result = new double[in.getLength().getAsInt()];
			int length = 0;
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);
				result[length++] = in.readDouble();
			}
		}
		else
		{
			int length = 0;
			double[] current = new double[512];
			while(in.peek() != Token.LIST_END)
			{
				in.next(Token.VALUE);

				if(length == current.length)
				{
					int newSize = ArraySerializer.growArray(current.length);
					current = Arrays.copyOf(current, newSize);
				}

				current[length++] = in.readDouble();
			}

			result = Arrays.copyOf(current, length);
		}

		in.next(Token.LIST_END);
		return result;
	}

	@Override
	public void write(double[] object, StreamingOutput out)
		throws IOException
	{
		out.writeListStart(object.length);
		for(double v : object)
		{
			out.writeDouble(v);
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
