package se.l4.exobytes.internal.streaming;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.OptionalInt;

import se.l4.exobytes.streaming.AbstractStreamingInput;
import se.l4.exobytes.streaming.StreamingFormat;
import se.l4.exobytes.streaming.Token;

/**
 * Input for binary format.  Available only for backwards compatibility
 * reasons, do not use for new code, it is recommended to use
 * {@link StreamingFormat#CBOR} instead.
 */
@Deprecated
public class LegacyBinaryInput
	extends AbstractStreamingInput
{
	private static final int CHARS_SIZE = 1024;
	private static final ThreadLocal<char[]> CHARS = new ThreadLocal<char[]>()
	{
		@Override
		protected char[] initialValue()
		{
			return new char[1024];
		}
	};

	private final InputStream in;

	private final byte[] buffer;

	private int peekedByte;
	private int currentValueByte;

	public LegacyBinaryInput(InputStream in)
		throws IOException
	{
		this.in = in;
		buffer = new byte[8];

		peekedByte = in.read();
	}

	@Override
	public void close()
		throws IOException
	{
		in.close();
	}

	@Override
	protected Token peek0()
		throws IOException
	{
		switch(peekedByte)
		{
			case -1:
				return Token.END_OF_STREAM;
			case LegacyBinaryOutput.TAG_KEY:
				return Token.VALUE;
			case LegacyBinaryOutput.TAG_OBJECT_START:
				return Token.OBJECT_START;
			case LegacyBinaryOutput.TAG_OBJECT_END:
				return Token.OBJECT_END;
			case LegacyBinaryOutput.TAG_LIST_START:
				return Token.LIST_START;
			case LegacyBinaryOutput.TAG_LIST_END:
				return Token.LIST_END;
			case LegacyBinaryOutput.TAG_NULL:
				return Token.NULL;
			default:
				return Token.VALUE;
		}
	}

	@Override
	protected Token next0()
		throws IOException
	{
		Token current = peek();
		if(current == Token.VALUE)
		{
			// Read actual data of keys and values
			currentValueByte = peekedByte;
			didReadValue = false;
		}
		else
		{
			if(current == Token.NULL)
			{
				currentValueByte = peekedByte;
			}

			peekedByte = in.read();
		}

		return current;
	}

	@Override
	public OptionalInt getLength()
	{
		return OptionalInt.empty();
	}

	@Override
	protected void skipValue()
		throws IOException
	{
		readDynamic();
	}

	private void readBuffer(int len)
		throws IOException
	{
		int n = 0;
		while(n < len)
		{
			int count = in.read(buffer, n, len - n);
			if(count < 0)
			{
				throw new EOFException("Expected to read " + len + " bytes, but could only read " + n);
			}
			n += count;
		}
	}

	private double readRawDouble()
		throws IOException
	{
		readBuffer(8);
		long value = ((long) buffer[0] & 0xff) |
			((long) buffer[1] & 0xff) << 8 |
			((long) buffer[2] & 0xff) << 16 |
			((long) buffer[3] & 0xff) << 24 |
			((long) buffer[4] & 0xff) << 32 |
			((long) buffer[5] & 0xff) << 40 |
			((long) buffer[6] & 0xff) << 48 |
			((long) buffer[7] & 0xff) << 56;

		return Double.longBitsToDouble(value);
	}

	private float readRawFloat()
		throws IOException
	{
		readBuffer(4);
		int value = (buffer[0] & 0xff) |
			(buffer[1] & 0xff) << 8 |
			(buffer[2] & 0xff) << 16 |
			(buffer[3] & 0xff) << 24;

		return Float.intBitsToFloat(value);
	}

	private int readRawInteger()
		throws IOException
	{
		int shift = 0;
		int result = 0;
		while(shift < 32)
		{
			final byte b = (byte) in.read();
			result |= (b & 0x7F) << shift;
			if((b & 0x80) == 0) return result;

			shift += 7;
		}

		throw new EOFException("Invalid integer");
	}

	private long readRawLong()
		throws IOException
	{
		int shift = 0;
		long result = 0;
		while(shift < 64)
		{
			final byte b = (byte) in.read();
			result |= (long) (b & 0x7F) << shift;
			if((b & 0x80) == 0) return result;

			shift += 7;
		}

		throw new EOFException("Invalid long");
	}

	private String readRawString()
		throws IOException
	{
		int length = readRawInteger();
		char[] chars = length < CHARS_SIZE ? CHARS.get() : new char[length];

		for(int i=0; i<length; i++)
		{
			int c = in.read() & 0xff;
			int t = c >> 4;
			if(t > -1 && t < 8)
			{
				chars[i] = (char) c;
			}
			else if(t == 12 || t == 13)
			{
				chars[i] = (char) ((c & 0x1f) << 6 | in.read() & 0x3f);
			}
			else if(t == 14)
			{
				chars[i] = (char) ((c & 0x0f) << 12
					| (in.read() & 0x3f) << 6
					| (in.read() & 0x3f) << 0);
			}
		}

		return new String(chars, 0, length);
	}

	private byte[] readRawByteArray()
		throws IOException
	{
		int length = readRawInteger();
		byte[] buffer = new byte[length];

		int n = 0;
		while(n < length)
		{
			int count = in.read(buffer, n, length - n);
			if(count < 0)
			{
				throw new EOFException("Expected to read " + length + " bytes, but could only read " + n);
			}
			n += count;
		}

		return buffer;
	}

	protected Object readDynamic0()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_BOOLEAN:
				return readBoolean();
			case LegacyBinaryOutput.TAG_DOUBLE:
				return readDouble();
			case LegacyBinaryOutput.TAG_FLOAT:
				return readFloat();
			case LegacyBinaryOutput.TAG_INT:
			case LegacyBinaryOutput.TAG_POSITIVE_INT:
			case LegacyBinaryOutput.TAG_NEGATIVE_INT:
				return readInt();
			case LegacyBinaryOutput.TAG_LONG:
			case LegacyBinaryOutput.TAG_POSITIVE_LONG:
			case LegacyBinaryOutput.TAG_NEGATIVE_LONG:
				return readLong();
			case LegacyBinaryOutput.TAG_KEY:
			case LegacyBinaryOutput.TAG_STRING:
				return readString();
			case LegacyBinaryOutput.TAG_BYTE_ARRAY:
				return readByteArray();
			case LegacyBinaryOutput.TAG_NULL:
				return null;
			default:
				throw new IOException("Unexpected value type, no idea what to do (type was " + currentValueByte + ")");
		}
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_BOOLEAN:
				int b = in.read();
				markValueRead();
				return b == 1;
			default:
				throw raiseException("Expected boolean, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		int value = readInt();
		if(value < Byte.MIN_VALUE || value > Byte.MAX_VALUE)
		{
			throw raiseException("Expected byte, but " + value + " is outside valid range");
		}
		return (byte) value;
	}

	@Override
	public short readShort()
		throws IOException
	{
		int value = readInt();
		if(value < Short.MIN_VALUE || value > Short.MAX_VALUE)
		{
			throw raiseException("Expected short, but " + value + " is outside valid range");
		}
		return (short) value;
	}

	@Override
	public char readChar()
		throws IOException
	{
		int value = readInt();
		if(value < Character.MIN_VALUE || value > Character.MAX_VALUE)
		{
			throw raiseException("Expected char, but " + value + " is outside valid range");
		}
		return (char) value;
	}

	@Override
	public int readInt()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_INT:
			{
				int i = readRawInteger();
				i = (i >>> 1) ^ -(i & 1);
				markValueRead();
				return i;
			}
			case LegacyBinaryOutput.TAG_POSITIVE_INT:
			{
				int i = readRawInteger();
				markValueRead();
				return i;
			}
			case LegacyBinaryOutput.TAG_NEGATIVE_INT:
			{
				int i = - readRawInteger();
				markValueRead();
				return i;
			}

			case LegacyBinaryOutput.TAG_LONG:
			case LegacyBinaryOutput.TAG_POSITIVE_LONG:
			case LegacyBinaryOutput.TAG_NEGATIVE_LONG:
				long v = readLong();
				if(v < Integer.MIN_VALUE || v > Integer.MAX_VALUE)
				{
					throw raiseException("Expected int, but " + v + " is outside valid range");
				}
				return (int) v;

			default:
				throw raiseException("Expected int, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_LONG:
			{
				long l = readRawLong();
				l = (l >>> 1) ^ -(l & 1);
				markValueRead();
				return l;
			}
			case LegacyBinaryOutput.TAG_POSITIVE_LONG:
			{
				long l = readRawLong();
				markValueRead();
				return l;
			}
			case LegacyBinaryOutput.TAG_NEGATIVE_LONG:
			{
				long l = - readRawLong();
				markValueRead();
				return l;
			}

			case LegacyBinaryOutput.TAG_INT:
			case LegacyBinaryOutput.TAG_POSITIVE_INT:
			case LegacyBinaryOutput.TAG_NEGATIVE_INT:
				return readInt();

			default:
				throw raiseException("Expected long, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public float readFloat()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_FLOAT:
				float f = readRawFloat();
				markValueRead();
				return f;

			case LegacyBinaryOutput.TAG_DOUBLE:
				double d = readRawDouble();
				markValueRead();
				return (float) d;

			default:
				throw raiseException("Expected float, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_DOUBLE:
				double d = readRawDouble();
				markValueRead();
				return d;

			case LegacyBinaryOutput.TAG_FLOAT:
				float f = readRawFloat();
				markValueRead();
				return f;

			default:
				throw raiseException("Expected double, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_STRING:
			case LegacyBinaryOutput.TAG_KEY:
				String s = readRawString();
				markValueRead();
				return s;
			default:
				throw raiseException("Expected string, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		switch(currentValueByte)
		{
			case LegacyBinaryOutput.TAG_BYTE_ARRAY:
				byte[] b = readRawByteArray();
				markValueRead();
				return b;
			default:
				throw raiseException("Expected bytes, but found " + valueType(currentValueByte));
		}
	}

	@Override
	public InputStream readByteStream()
		throws IOException
	{
		return new ByteArrayInputStream(readByteArray());
	}

	protected void markValueRead()
		throws IOException
	{
		super.markValueRead();
		peekedByte = in.read();
	}

	private String valueType(int b)
		throws IOException
	{
		switch(b)
		{
			case LegacyBinaryOutput.TAG_BOOLEAN:
				return "boolean";
			case LegacyBinaryOutput.TAG_DOUBLE:
				return "double";
			case LegacyBinaryOutput.TAG_FLOAT:
				return "float";
			case LegacyBinaryOutput.TAG_INT:
			case LegacyBinaryOutput.TAG_POSITIVE_INT:
			case LegacyBinaryOutput.TAG_NEGATIVE_INT:
				return "int";
			case LegacyBinaryOutput.TAG_LONG:
			case LegacyBinaryOutput.TAG_POSITIVE_LONG:
			case LegacyBinaryOutput.TAG_NEGATIVE_LONG:
				return "long";
			case LegacyBinaryOutput.TAG_NULL:
				return "null";
			case LegacyBinaryOutput.TAG_STRING:
				return "string";
			case LegacyBinaryOutput.TAG_BYTE_ARRAY:
				return "byte array";
			default:
				throw raiseException("Unexpected value type, no idea what to do (read byte was " + b + ")");
		}
	}
}
