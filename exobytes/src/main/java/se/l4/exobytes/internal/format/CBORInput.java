package se.l4.exobytes.internal.format;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.OptionalInt;

import se.l4.commons.io.Bytes;
import se.l4.exobytes.format.AbstractStreamingInput;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.Token;

/**
 * {@link StreamingInput} that reads CBOR encoded data.
 */
public class CBORInput
	extends AbstractStreamingInput
{
	private static final byte[] EMPTY_BYTES = new byte[0];

	private final int LEVELS = 20;

	private final InputStream in;

	private int peekedByte;
	private int currentByte;

	private int[] remainingReads;
	private boolean[] listOrMap;
	private int level;

	private boolean didReadValue;
	private OptionalInt length;

	public CBORInput(InputStream in)
	{
		this.in = in;

		remainingReads = new int[LEVELS];
		listOrMap = new boolean[LEVELS];

		listOrMap[0] = true;
		remainingReads[0] = -1;

		peekedByte = -2;

		length = OptionalInt.empty();
	}

	@Override
	public Token peek()
		throws IOException
	{
		if(peekedByte == -2)
		{
			peekedByte = in.read();
			readType();
		}

		if(remainingReads[level] == 0)
		{
			/*
			 * If there are no more values to read in the list or object
			 * emulate an end.
			 */
			return listOrMap[level] ? Token.LIST_END : Token.OBJECT_END;
		}

		if(peekedByte == -1)
		{
			return Token.END_OF_STREAM;
		}

		switch(peekedByte >> 5)
		{
			case CborConstants.MAJOR_TYPE_ARRAY:
				return Token.LIST_START;
			case CborConstants.MAJOR_TYPE_MAP:
				return Token.OBJECT_START;
			case CborConstants.MAJOR_TYPE_SIMPLE:
				if((peekedByte & CborConstants.AI_INDEFINITE) == CborConstants.SIMPLE_TYPE_NULL)
				{
					return Token.NULL;
				}
				else if((peekedByte & CborConstants.AI_INDEFINITE) == CborConstants.AI_INDEFINITE)
				{
					return listOrMap[level] ? Token.LIST_END : Token.OBJECT_END;
				}
			case CborConstants.MAJOR_TYPE_UNSIGNED_INT:
			case CborConstants.MAJOR_TYPE_NEGATIVE_INT:
			case CborConstants.MAJOR_TYPE_BYTE_STRING:
			case CborConstants.MAJOR_TYPE_TEXT_STRING:
			case CborConstants.MAJOR_TYPE_TAGGED:
				// TODO: This needs to return KEY for every other item in a map
				if(! listOrMap[level])
				{
					int r = remainingReads[level] % 2;
					if(r == 0)
					{
						return Token.KEY;
					}
				}

				return Token.VALUE;
		}

		throw raiseException("Can't peek, unknown type");
	}

	@Override
	protected Token next0()
		throws IOException
	{
		Token nextToken = peek();
		currentByte = peekedByte;

		switch(nextToken)
		{
			case NULL:
				markValueRead();
				peekedByte = in.read();
				length = OptionalInt.empty();
				break;
			case LIST_START:
				markValueRead();
				peekedByte = in.read();
				increaseLevel(isIndeterminateLength() ? -2 : getLengthAsInt(), true);
				break;
			case OBJECT_START:
				markValueRead();
				peekedByte = in.read();
				increaseLevel(isIndeterminateLength() ? -2 : getLengthAsInt(), false);
				break;
			case LIST_END:
			case OBJECT_END:
				length = OptionalInt.empty();

				if(remainingReads[level] != 0)
				{
					// Peek the next byte if this wasn't a synthetic end event
					peekedByte = in.read();
				}

				readType();

				level--;
				break;
			default:
				length = OptionalInt.empty();

				peekedByte = in.read();
				break;
		}

		return nextToken;
	}

	@Override
	public OptionalInt getLength()
	{
		return length;
	}

	@Override
	public void close()
		throws IOException
	{
		in.close();
	}

	@Override
	public Object readDynamic()
		throws IOException
	{
		switch(majorType())
		{
			case CborConstants.MAJOR_TYPE_UNSIGNED_INT:
			case CborConstants.MAJOR_TYPE_NEGATIVE_INT:
				return readLong();
			case CborConstants.MAJOR_TYPE_BYTE_STRING:
				return readByteArray();
			case CborConstants.MAJOR_TYPE_TEXT_STRING:
				return readString();
			case CborConstants.MAJOR_TYPE_SIMPLE:
				switch(currentByte & 31)
				{
					case CborConstants.SIMPLE_TYPE_HALF:
					case CborConstants.SIMPLE_TYPE_FLOAT:
						return readFloat();
					case CborConstants.SIMPLE_TYPE_DOUBLE:
						return readDouble();
					case CborConstants.SIMPLE_TYPE_TRUE:
					case CborConstants.SIMPLE_TYPE_FALSE:
						return readBoolean();
					case CborConstants.SIMPLE_TYPE_NULL:
						return null;
				}
		}

		throw raiseException("Unknown value type");
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		checkReadable();

		boolean result;
		if(isSimpleType(CborConstants.SIMPLE_TYPE_TRUE))
		{
			result = true;
		}
		else if(isSimpleType(CborConstants.SIMPLE_TYPE_FALSE))
		{
			result = false;
		}
		else
		{
			throw raiseException("Unable to read boolean");
		}

		markValueRead();
		return result;
	}

	@Override
	public byte readByte()
		throws IOException
	{
		return (byte) readInt();
	}

	@Override
	public char readChar()
		throws IOException
	{
		return (char) readInt();
	}

	@Override
	public short readShort()
		throws IOException
	{
		return (short) readInt();
	}

	@Override
	public int readInt()
		throws IOException
	{
		checkReadable();

		int result;
		if(isMajorType(CborConstants.MAJOR_TYPE_UNSIGNED_INT))
		{
			result = getLengthAsInt();
		}
		else if(isMajorType(CborConstants.MAJOR_TYPE_NEGATIVE_INT))
		{
			result = -1 - getLengthAsInt();
		}
		else
		{
			throw raiseException("Unable to read int");
		}

		markValueRead();
		return result;
	}

	@Override
	public long readLong()
		throws IOException
	{
		checkReadable();

		long result;
		if(isMajorType(CborConstants.MAJOR_TYPE_UNSIGNED_INT))
		{
			result = getLengthAsLong();
		}
		else if(isMajorType(CborConstants.MAJOR_TYPE_NEGATIVE_INT))
		{
			result = -1 - getLengthAsLong();
		}
		else
		{
			throw raiseException("Unable to read long");
		}

		markValueRead();
		return result;
	}

	@Override
	public float readFloat()
		throws IOException
	{
		checkReadable();

		float result;
		if(isSimpleType(CborConstants.SIMPLE_TYPE_HALF))
		{
			// TODO: Support for half values
			throw raiseException("Half-values are not supported");
		}
		else if(isSimpleType(CborConstants.SIMPLE_TYPE_FLOAT))
		{
			result = readRawFloat();
		}
		else if(isSimpleType(CborConstants.SIMPLE_TYPE_DOUBLE))
		{
			double d = readRawDouble();
			if(d < Float.MIN_VALUE || d > Float.MAX_VALUE)
			{
				throw raiseException("Expected float but " + d + " is outside valid range");
			}

			result = (float) d;
		}
		else
		{
			throw raiseException("Unable to read float");
		}

		markValueRead();
		return result;
	}

	@Override
	public double readDouble()
		throws IOException
	{
		checkReadable();

		double result;
		if(isSimpleType(CborConstants.SIMPLE_TYPE_HALF))
		{
			// TODO: Support for half values
			throw raiseException("Half-values are not supported");
		}
		else if(isSimpleType(CborConstants.SIMPLE_TYPE_FLOAT))
		{
			result = readRawFloat();
		}
		else if(isSimpleType(CborConstants.SIMPLE_TYPE_DOUBLE))
		{
			result = readRawDouble();
		}
		else
		{
			throw raiseException("Unable to read float");
		}

		markValueRead();
		return result;
	}

	@Override
	public String readString()
		throws IOException
	{
		checkReadable();

		if(! isMajorType(CborConstants.MAJOR_TYPE_TEXT_STRING))
		{
			throw raiseException("Can not read string");
		}

		int length = getLengthAsInt();
		String result;
		if(length == 0)
		{
			result = "";
		}
		else if(length == CborConstants.AI_INDEFINITE)
		{
			StringBuilder builder = new StringBuilder();
			while(peekedByte != 0xff)
			{
				currentByte = read();
				if(! isMajorType(CborConstants.MAJOR_TYPE_TEXT_STRING))
				{
					throw raiseException("Expected chunked string, but could not read substring");
				}

				int subLength = getLengthAsInt();

				byte[] data = new byte[subLength];
				readFully(data, 0, data.length);

				builder.append(new String(data, StandardCharsets.UTF_8));
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked string");
			}

			result = builder.toString();
		}
		else
		{
			byte[] data = new byte[length];
			readFully(data, 0, data.length);

			result = new String(data, StandardCharsets.UTF_8);
		}

		markValueRead();
		return result;
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		checkReadable();

		if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
		{
			throw raiseException("Can not read string");
		}

		int length = getLengthAsInt();
		if(length == 0)
		{
			return EMPTY_BYTES;
		}
		else if(length == CborConstants.AI_INDEFINITE)
		{
			StringBuilder builder = new StringBuilder();
			byte[] data = EMPTY_BYTES;
			while(peekedByte != 0xff)
			{
				currentByte = read();
				if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
				{
					throw raiseException("Expected chunked bytes, but could not read substring");
				}

				int subLength = getLengthAsInt();

				int offset = data.length;
				data = Arrays.copyOf(data, data.length + subLength);
				readFully(data, offset, data.length);

				builder.append(new String(data, StandardCharsets.UTF_8));
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked bytes");
			}

			return data;
		}
		else
		{
			byte[] data = new byte[length];
			readFully(data, 0, length);
			markValueRead();
			return data;
		}
	}

	@Override
	public Bytes readBytes()
		throws IOException
	{
		return Bytes.create(readByteArray());
	}

	@Override
	public InputStream asInputStream()
		throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void readFully(byte[] buffer, int offset, int len)
		throws IOException
	{
		if(len == 0) return;

		buffer[offset] = (byte) peekedByte;

		int n = offset + 1;
		while(n < len)
		{
			int count = in.read(buffer, n, len - n);
			if(count < 0)
			{
				throw new EOFException("Expected to read " + len + " bytes, but could only read " + n);
			}
			n += count;
		}

		peekedByte = in.read();
	}

	private float readRawFloat()
		throws IOException
	{
		int i = read() << 24
			| read() << 16
			| read() << 8
			| read();
		return Float.intBitsToFloat(i);
	}

	private double readRawDouble()
		throws IOException
	{
		long l = ((long) read()) << 56
			| (long) read() << 48
			| (long) read() << 40
			| (long) read() << 32
			| (long) read() << 24
			| read() << 16
			| read() << 8
			| read();
		return Double.longBitsToDouble(l);
	}

	private void checkReadable()
		throws IOException
	{
		if(currentByte == -1)
		{
			throw new EOFException();
		}
	}

	private int majorType()
	{
		return currentByte >> 5;
	}

	private boolean isMajorType(int majorType)
	{
		return currentByte >> 5 == majorType;
	}

	private boolean isSimpleType(int simpleType)
	{
		return currentByte >> 5 == CborConstants.MAJOR_TYPE_SIMPLE && (currentByte & 31) == simpleType;
	}

	private boolean isIndeterminateLength()
	{
		return (currentByte & 31) == CborConstants.AI_INDEFINITE;
	}

	private int getLengthAsInt()
		throws IOException
	{
		switch(currentByte & 31)
		{
			case CborConstants.AI_ONE_BYTE:
				return read();
			case CborConstants.AI_TWO_BYTES:
				return read() << 8
					| read();
			case CborConstants.AI_FOUR_BYTES:
				return read() << 24
					| read() << 16
					| read() << 8
					| read();
			case CborConstants.AI_EIGHT_BYTES:
				throw raiseException("Value does not fit within an int, consider using readLong");
			default:
				return currentByte & 31;
		}
	}

	private long getLengthAsLong()
		throws IOException
	{
		switch(currentByte & 31)
		{
			case CborConstants.AI_ONE_BYTE:
				return read();
			case CborConstants.AI_TWO_BYTES:
				return read() << 8
					| read();
			case CborConstants.AI_FOUR_BYTES:
				return read() << 24
					| read() << 16
					| read() << 8
					| read();
			case CborConstants.AI_EIGHT_BYTES:
				return ((long) read()) << 56
					| (long) read() << 48
					| (long) read() << 40
					| (long) read() << 32
					| (long) read() << 24
					| read() << 16
					| read() << 8
					| read();
			default:
				return currentByte & 31;
		}
	}

	private void increaseLevel(int expectedCount, boolean isList)
	{
		level++;
		if(listOrMap.length == level)
		{
			// Grow lists when needed
			listOrMap = Arrays.copyOf(listOrMap, level * 2);
			remainingReads = Arrays.copyOf(remainingReads, level * 2);
		}

		listOrMap[level] = isList;
		remainingReads[level] = isList ? expectedCount : expectedCount * 2;

		length = expectedCount < 0 ? OptionalInt.empty() : OptionalInt.of(expectedCount);
	}

	private int read()
		throws IOException
	{
		if(peekedByte == -1)
		{
			throw new EOFException();
		}

		int v = currentByte = peekedByte;
		peekedByte = in.read();
		return v;
	}

	private void markValueRead()
		throws IOException
	{
		didReadValue = true;

		int count = remainingReads[level];
		if(count == 0)
		{
			throw raiseException("Tried to read more values than expected");
		}

		remainingReads[level] = count - 1;

		readType();
	}

	private void readType()
		throws IOException
	{
		while(peekedByte >> 5 == CborConstants.MAJOR_TYPE_TAGGED)
		{
			// TODO: What do we do with these types?
			read();
		}
	}
}
