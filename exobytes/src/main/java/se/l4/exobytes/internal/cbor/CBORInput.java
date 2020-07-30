package se.l4.exobytes.internal.cbor;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.OptionalInt;

import se.l4.exobytes.streaming.AbstractStreamingInput;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;
import se.l4.exobytes.streaming.ValueConversion;

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

	private int length;
	private char[] reusableChars = new char[64];

	public CBORInput(InputStream in)
		throws IOException
	{
		this.in = in;

		remainingReads = new int[LEVELS];
		listOrMap = new boolean[LEVELS];

		listOrMap[0] = true;
		remainingReads[0] = -1;

		length = -1;

		peekedByte = in.read();
		readType();
	}

	@Override
	protected Token peek0()
		throws IOException
	{
		if(remainingReads[level] == 0)
		{
			/*
			 * If there are no more values to read in the list or object
			 * emulate an end token.
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
				if(! listOrMap[level] && remainingReads[level] % 2 == 0)
				{
					// Reading an object and the next token is the key
					return Token.KEY;
				}

				return Token.VALUE;
		}

		throw raiseException("Can't peek, unknown type");
	}

	@Override
	protected Token next0()
		throws IOException
	{
		if(remainingReads[level] == 0)
		{
			/*
			 * If there are no more values to read in the list or object
			 * emulate an end token.
			 */
			length = -1;
			readType();

			return listOrMap[level--] ? Token.LIST_END : Token.OBJECT_END;
		}

		if(peekedByte == -1)
		{
			return Token.END_OF_STREAM;
		}

		currentByte = peekedByte;
		switch(peekedByte >> 5)
		{
			case CborConstants.MAJOR_TYPE_ARRAY:
				markValueRead();
				peekedByte = in.read();
				increaseLevel(isIndeterminateLength() ? -2 : getLengthAsInt(), true);
				return Token.LIST_START;
			case CborConstants.MAJOR_TYPE_MAP:
				markValueRead();
				peekedByte = in.read();
				increaseLevel(isIndeterminateLength() ? -2 : getLengthAsInt(), false);
				return Token.OBJECT_START;
			case CborConstants.MAJOR_TYPE_SIMPLE:
				switch(peekedByte)
				{
					case 246:
						markValueRead();
						peekedByte = in.read();
						length = -1;
						return Token.NULL;
					case 255:
						length = -1;

						// Peek the next byte if this wasn't a synthetic end event
						peekedByte = in.read();

						readType();

						return listOrMap[level--] ? Token.LIST_END : Token.OBJECT_END;
				}
			case CborConstants.MAJOR_TYPE_UNSIGNED_INT:
			case CborConstants.MAJOR_TYPE_NEGATIVE_INT:
			case CborConstants.MAJOR_TYPE_BYTE_STRING:
			case CborConstants.MAJOR_TYPE_TEXT_STRING:
				length = -1;

				peekedByte = in.read();
				if(! listOrMap[level] && remainingReads[level] % 2 == 0)
				{
					// Reading an object and the next token is the key
					return Token.KEY;
				}

				return Token.VALUE;
		}

		throw raiseException("Unsupported CBOR stream encountered byte " + currentByte);
	}

	@Override
	public OptionalInt getLength()
	{
		return length < 0 ? OptionalInt.empty() : OptionalInt.of(length);
	}

	@Override
	public void close()
		throws IOException
	{
		in.close();
	}

	@Override
	protected void skipKeyOrValue()
		throws IOException
	{
		switch(majorType())
		{
			case CborConstants.MAJOR_TYPE_UNSIGNED_INT:
			case CborConstants.MAJOR_TYPE_NEGATIVE_INT:
				skipLength();
				break;
			case CborConstants.MAJOR_TYPE_BYTE_STRING:
				skipByteArray();
				break;
			case CborConstants.MAJOR_TYPE_TEXT_STRING:
				skipString();
				break;
			case CborConstants.MAJOR_TYPE_SIMPLE:
				switch(currentByte & 31)
				{
					case CborConstants.SIMPLE_TYPE_HALF:
						skipBytes(2);
						break;
					case CborConstants.SIMPLE_TYPE_FLOAT:
						skipBytes(4);
						break;
					case CborConstants.SIMPLE_TYPE_DOUBLE:
						skipBytes(8);
						break;
					case CborConstants.SIMPLE_TYPE_TRUE:
					case CborConstants.SIMPLE_TYPE_FALSE:
						break;
				}
		}

		markValueRead();
	}

	@Override
	protected Object readDynamic0()
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
		if(! isMajorType(CborConstants.MAJOR_TYPE_SIMPLE))
		{
			throw raiseException("Unable to read boolean");
		}

		switch(currentByte & 31)
		{
			case CborConstants.SIMPLE_TYPE_TRUE:
				markValueRead();
				return true;
			case CborConstants.SIMPLE_TYPE_FALSE:
				markValueRead();
				return false;
			default:
				throw raiseException("Unable to read boolean");
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		return ValueConversion.toByte(readInt());
	}

	@Override
	public char readChar()
		throws IOException
	{
		return ValueConversion.toChar(readInt());
	}

	@Override
	public short readShort()
		throws IOException
	{
		return ValueConversion.toShort(readInt());
	}

	@Override
	public int readInt()
		throws IOException
	{
		int majorType = majorType();
		switch(majorType)
		{
			case CborConstants.MAJOR_TYPE_UNSIGNED_INT:
			{
				int result = getLengthAsInt();
				markValueRead();
				return result;
			}
			case CborConstants.MAJOR_TYPE_NEGATIVE_INT:
			{
				int result = -1 - getLengthAsInt();
				markValueRead();
				return result;
			}
			default:
				throw raiseException("Unable to read int");
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
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
		if(! isMajorType(CborConstants.MAJOR_TYPE_SIMPLE))
		{
			throw raiseException("Unable to read float");
		}

		switch(currentByte & 31)
		{
			case CborConstants.SIMPLE_TYPE_FLOAT:
				float f = readRawFloat();
				markValueRead();
				return f;
			case CborConstants.SIMPLE_TYPE_DOUBLE:
				double d = readRawDouble();
				markValueRead();
				return (float) d;
			default:
				throw raiseException("Unable to read float");
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		if(! isMajorType(CborConstants.MAJOR_TYPE_SIMPLE))
		{
			throw raiseException("Unable to read double");
		}

		switch(currentByte & 31)
		{
			case CborConstants.SIMPLE_TYPE_FLOAT:
				double f = readRawFloat();
				markValueRead();
				return f;
			case CborConstants.SIMPLE_TYPE_DOUBLE:
				double d = readRawDouble();
				markValueRead();
				return d;
			default:
				throw raiseException("Unable to read double");
		}
	}

	@Override
	public String readString()
		throws IOException
	{
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
				builder.append(readString(subLength));
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked string");
			}

			result = builder.toString();
		}
		else
		{
			result = readString(length);
		}

		markValueRead();
		return result;
	}

	private String readString(int byteLength)
		throws IOException
	{
		char[] chars = this.reusableChars;

		int offset = 0;
		int c = peekedByte;
		while(offset < byteLength)
		{
			if(c >= 0x80)
			{
				break;
			}

			if(chars.length == offset)
			{
				chars = this.reusableChars = Arrays.copyOf(chars, chars.length * 2);
			}

			chars[offset++] = (char) c;
			c = in.read();
		}

		int read = offset;
		while(read < byteLength)
		{
			if((c & 0x80) == 0)
			{
				if(chars.length == offset)
				{
					chars = this.reusableChars = Arrays.copyOf(chars, chars.length * 2);
				}

				read += 1;
				chars[offset++] = (char) c;
			}
			else if((c & 0xe0) == 0xc0)
			{
				if(chars.length == offset)
				{
					chars = this.reusableChars = Arrays.copyOf(chars, chars.length * 2);
				}

				read += 2;
				chars[offset++] = (char) (((c & 0x1f) << 6)
					| (in.read() & 0x3f));
			}
			else if((c & 0xf0) == 0xe0)
			{
				if(chars.length == offset)
				{
					chars = this.reusableChars = Arrays.copyOf(chars, chars.length * 2);
				}

				read += 3;
				chars[offset++] = (char) (((c & 0x0f) << 12)
					| (in.read() & 0x3f) << 6
					| (in.read() & 0x3f));
			}
			else
			{
				int v;
				if((c & 0xf8) == 0xf0)
				{
					if(chars.length == offset)
					{
						chars = this.reusableChars = Arrays.copyOf(chars, chars.length * 2);
					}

					v = ((c & 0x07) << 18)
						| ((in.read() & 0x3f) << 12)
						| ((in.read() & 0x3f) << 6)
						| (in.read() & 0x3F);

					read += 4;
				}
				else
				{
					throw raiseException("Tried reading a string but encountered invalid UTF-8 sequence");
				}

				if(v > 0x10000)
				{
					// This is a surrogate - let's read the next char
					int supplement = v - 0x10000;
					chars[offset++] = (char) ((supplement >> 10) | 0xd800);
					chars[offset++] = (char) ((supplement & 0x3ff) | 0xdc00);
				}
				else
				{
					chars[offset++] = (char) v;
				}
			}

			c = in.read();
		}

		peekedByte = c;

		return new String(chars, 0, offset);
	}

	private void skipString()
		throws IOException
	{
		int length = getLengthAsInt();
		if(length == CborConstants.AI_INDEFINITE)
		{
			while(peekedByte != 0xff)
			{
				currentByte = read();
				if(! isMajorType(CborConstants.MAJOR_TYPE_TEXT_STRING))
				{
					throw raiseException("Expected chunked string, but could not read substring");
				}

				int subLength = getLengthAsInt();
				skipBytes(subLength);
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked string");
			}
		}
		else if(length > 0)
		{
			skipBytes(length);
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
		{
			throw raiseException("Can not read bytes");
		}

		int length = getLengthAsInt();
		if(length == 0)
		{
			markValueRead();
			return EMPTY_BYTES;
		}
		else if(length == CborConstants.AI_INDEFINITE)
		{
			byte[] data = EMPTY_BYTES;
			while(peekedByte != 0xff)
			{
				currentByte = read();
				if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
				{
					throw raiseException("Expected chunked bytes, but could not read byte chunk");
				}

				int subLength = getLengthAsInt();

				int offset = data.length;
				data = Arrays.copyOf(data, data.length + subLength);
				readFully(data, offset, data.length);
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked bytes");
			}

			markValueRead();
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

	private void skipByteArray()
		throws IOException
	{
		int length = getLengthAsInt();
		if(length == CborConstants.AI_INDEFINITE)
		{
			while(peekedByte != 0xff)
			{
				currentByte = read();
				if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
				{
					throw raiseException("Expected chunked bytes, but could not read byte chunk");
				}

				int subLength = getLengthAsInt();
				skipBytes(subLength);
			}

			if(read() != 0xff)
			{
				throw raiseException("Expected end of chunked bytes");
			}
		}
		else if(length > 0)
		{
			skipBytes(length);
		}
	}

	@Override
	public InputStream readByteStream()
		throws IOException
	{
		if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
		{
			throw raiseException("Can not read bytes");
		}

		int length = getLengthAsInt();
		if(length == 0)
		{
			markValueRead();
			return new ByteArrayInputStream(EMPTY_BYTES);
		}
		else if(length == CborConstants.AI_INDEFINITE)
		{
			return new CBORChunkedInputStream(new CBORChunkedInputStream.Control()
			{
				@Override
				public int readChunkLength()
					throws IOException
				{
					if(peekedByte == 0xff)
					{
						return -1;
					}

					currentByte = read();
					if(! isMajorType(CborConstants.MAJOR_TYPE_BYTE_STRING))
					{
						throw raiseException("Expected chunked bytes, but could not read byte chunk");
					}

					return getLengthAsInt();
				}

				@Override
				public int read()
					throws IOException
				{
					return CBORInput.this.read();
				}

				@Override
				public int read(byte[] buf, int offset, int length)
					throws IOException
				{
					if(peekedByte == -1) return -1;
					if(length == 0) return 0;

					buf[offset] = (byte) peekedByte;

					int read = in.read(buf, offset + 1, length - 1) + 1;
					peekedByte = in.read();
					return read;
				}

				@Override
				public void skip(int bytes)
					throws IOException
				{
					skipBytes(bytes);
				}

				@Override
				public void close()
					throws IOException
				{
					if(read() != 0xff)
					{
						throw raiseException("Expected end of chunked bytes");
					}

					markValueRead();
				}
			});
		}
		else
		{
			byte[] data = new byte[length];
			readFully(data, 0, length);
			markValueRead();
			return new ByteArrayInputStream(data);
		}
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

	/**
	 * Skip a certain amount of bytes taking the peeked byte into account.
	 *
	 * @param length
	 * @throws IOException
	 */
	private void skipBytes(int length)
		throws IOException
	{
		for(int i=0, n=length-1; i<n; i++)
		{
			in.read();
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

	private int majorType()
	{
		return currentByte >> 5;
	}

	private boolean isMajorType(int majorType)
	{
		return currentByte >> 5 == majorType;
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
				int v = read() << 24
					| read() << 16
					| read() << 8
					| read();

				if(v < 0)
				{
					throw new IOException("Tried to read int but can not safely convert, value overflowed");
				}

				return v;
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

	private void skipLength()
		throws IOException
	{
		switch(currentByte & 31)
		{
			case CborConstants.AI_ONE_BYTE:
				skipBytes(1);
				break;
			case CborConstants.AI_TWO_BYTES:
				skipBytes(2);
				break;
			case CborConstants.AI_FOUR_BYTES:
				skipBytes(4);
				break;
			case CborConstants.AI_EIGHT_BYTES:
				skipBytes(8);
				break;
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

		length = expectedCount;
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

	protected void markValueRead()
		throws IOException
	{
		super.markValueRead();

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
