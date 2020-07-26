package se.l4.exobytes.streaming;

import java.io.IOException;

/**
 * Static utilities for converting values and throwing the expected errors if
 * the values can not be converted.
 */
public class ValueConversion
{
	private ValueConversion()
	{
	}

	public static byte toByte(int i)
		throws IOException
	{
		if(i < Byte.MIN_VALUE || i > Byte.MAX_VALUE)
		{
			throw new IOException("Tried to read byte but can not safely convert, value was: " + i);
		}

		return (byte) i;
	}

	public static short toShort(int i)
		throws IOException
	{
		if(i < Short.MIN_VALUE || i > Short.MAX_VALUE)
		{
			throw new IOException("Tried to read short but can not safely convert, value was: " + i);
		}

		return (short) i;
	}

	public static char toChar(int i)
		throws IOException
	{
		if(i < Character.MIN_VALUE || i > Character.MAX_VALUE)
		{
			throw new IOException("Tried to read char but can not safely convert, value was: " + i);
		}

		return (char) i;
	}

	public static int toInt(long i)
		throws IOException
	{
		if(i < Integer.MIN_VALUE || i > Integer.MAX_VALUE)
		{
			throw new IOException("Tried to read int but can not safely convert, value was: " + i);
		}

		return (int) i;
	}

	public static float toFloat(double f)
		throws IOException
	{
		if(f < Float.MIN_VALUE || f > Float.MAX_VALUE)
		{
			throw new IOException("Tried to read int but can not safely convert, value was: " + f);
		}

		return (float) f;
	}
}
