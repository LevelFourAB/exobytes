package se.l4.exobytes.internal.cbor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import se.l4.exobytes.streaming.StreamingOutput;

/**
 * {@link StreamingOutput} that writes data in a CBOR format.
 */
public class CBOROutput
	implements StreamingOutput
{
	private static final int LEVELS = 20;

	private final OutputStream out;

	private byte[] buffer;
	private int index;

	/**
	 * If the current level is using an indeterminate length and needs a break.
	 */
	private int[] remainingWrites;
	private int level;

	public CBOROutput(OutputStream out)
	{
		this.out = out;

		this.buffer = new byte[32];

		remainingWrites = new int[LEVELS];
		remainingWrites[0] = -1;
	}

	@Override
	public void close()
		throws IOException
	{
		flushBuffer();
		out.close();
	}

	@Override
	public void flush()
		throws IOException
	{
		flushBuffer();
		out.flush();
	}

	@Override
	public void writeByte(byte b)
		throws IOException
	{
		writeInt(b);
	}

	@Override
	public void writeShort(short s)
		throws IOException
	{
		writeInt(s);
	}

	@Override
	public void writeChar(char c)
		throws IOException
	{
		writeInt(c);
	}

	@Override
	public void writeInt(int number)
		throws IOException
	{
		consumeWrite();

		if(number >= 0)
		{
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_UNSIGNED_INT, number);
		}
		else
		{
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_NEGATIVE_INT, -1 - number);
		}
	}

	@Override
	public void writeLong(long number)
		throws IOException
	{
		consumeWrite();

		if(number >= 0)
		{
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_UNSIGNED_INT, number);
		}
		else
		{
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_NEGATIVE_INT, -1 - number);
		}
	}

	@Override
	public void writeBoolean(boolean b)
		throws IOException
	{
		consumeWrite();

		ensure(1);
		buffer[index++] = (byte) ((CborConstants.MAJOR_TYPE_SIMPLE << 5) | (b ? CborConstants.SIMPLE_TYPE_TRUE : CborConstants.SIMPLE_TYPE_FALSE));
	}

	@Override
	public void writeNull()
		throws IOException
	{
		consumeWrite();

		buffer[index++] = (byte) ((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_NULL);
	}

	@Override
	public void writeFloat(float number)
		throws IOException
	{
		consumeWrite();

		int bits = Float.floatToRawIntBits(number);

		ensure(5);
		buffer[index++] = (byte) ((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_FLOAT);
		buffer[index++] = (byte) ((bits >> 24) & 0xff);
		buffer[index++] = (byte) ((bits >> 16) & 0xff);
		buffer[index++] = (byte) ((bits >> 8) & 0xff);
		buffer[index++] = (byte) (bits & 0xff);
	}

	@Override
	public void writeDouble(double number)
		throws IOException
	{
		consumeWrite();

		long bits = Double.doubleToRawLongBits(number);

		ensure(9);
		buffer[index++] = (byte) ((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_DOUBLE);
		buffer[index++] = (byte) ((bits >> 56) & 0xff);
		buffer[index++] = (byte) ((bits >> 48) & 0xff);
		buffer[index++] = (byte) ((bits >> 40) & 0xff);
		buffer[index++] = (byte) ((bits >> 32) & 0xff);
		buffer[index++] = (byte) ((bits >> 24) & 0xff);
		buffer[index++] = (byte) ((bits >> 16) & 0xff);
		buffer[index++] = (byte) ((bits >> 8) & 0xff);
		buffer[index++] = (byte) (bits & 0xff);
	}

	@Override
	public void writeString(String value)
		throws IOException
	{
		consumeWrite();

		// TODO: There is probably a point where a huge string is better written streaming

		int length = value.length();
		int byteLength = calculateUTF8Length(value);
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_TEXT_STRING, byteLength);

		if(length == byteLength)
		{
			writeStringASCII(value);
		}
		else
		{
			writeStringUTF8(value, byteLength);
		}
	}

	private void writeStringASCII(String s)
		throws IOException
	{
		int length = s.length();
		ensure(length);
		for(int i=0; i<length; i++)
		{
			buffer[index++] = (byte) s.charAt(i);
		}
	}

	private void writeStringUTF8(String value, int byteLength)
		throws IOException
	{
		ensure(byteLength);
		byte[] buffer = this.buffer;

		int length = value.length();

		int writeIdx = index;
		int charIdx = 0;

		/*
		 * Write as many pure ASCII characters as possible. As soon as a char
		 * that needs multi byte encoding is seen stop.
		 */
		while(charIdx < length)
		{
			char c = value.charAt(charIdx);
			if(c >= 0x80) break;

			buffer[writeIdx++] = (byte) c;
			charIdx++;
		}

		/*
		 * Write the remaining characters that may be a mix of multi-byte and
		 * non multi-byte chars.
		 */
		while(charIdx < length)
		{
			char c = value.charAt(charIdx++);

			if(c < 0x80)
			{
				buffer[writeIdx++] = (byte) c;
			}
			else if(c < 0x800)
			{
				buffer[writeIdx++] = (byte) (0xc0 | c >> 6 & 0x1f);
				buffer[writeIdx++] = (byte) (0x80 | c >> 0 & 0x3f);
			}
			else if(! Character.isSurrogate(c))
			{
				buffer[writeIdx++] = (byte) (0xe0 | c >> 12 & 0x0f);
				buffer[writeIdx++] = (byte) (0x80 | c >> 6 & 0x3f);
				buffer[writeIdx++] = (byte) (0x80 | c >> 0 & 0x3f);
			}
			else
			{
				char c2 = value.charAt(charIdx++);

				// TODO: This needs quick sanity check if the one in calculateUTF8Length is not enough

				int codePoint = Character.toCodePoint(c, c2);
				buffer[writeIdx++] = (byte) ((0xf0) | (codePoint >>> 18));
				buffer[writeIdx++] = (byte) (0x80 | (0x3f & (codePoint >>> 12)));
				buffer[writeIdx++] = (byte) (0x80 | (0x3f & (codePoint >>> 6)));
				buffer[writeIdx++] = (byte) (0x80 | (0x3f & codePoint));
			}
		}

		index = writeIdx;
	}

	private int calculateUTF8Length(String value)
		throws IOException
	{
		int length = value.length();
		int result = length;

		int i =0;
		while(i < length && value.charAt(i) < 0x80)
		{
			i++;
		}

		while(i < length)
		{
			char c = value.charAt(i);

			if(c < 0x800)
			{
				result += ((0x7f - c) >>> 31);
			}
			else
			{
				result += 2;

				if(Character.isSurrogate(c))
				{
					int codePoint = Character.codePointAt(value, i);
					if(codePoint < Character.MIN_SUPPLEMENTARY_CODE_POINT)
					{
						throw new IOException();
					}

					i++;
				}
			}

			i++;
		}

		return result;
	}

	@Override
	public void writeByteArray(byte[] data)
		throws IOException
	{
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_BYTE_STRING, data.length);
		write(data, 0, data.length);
	}

	@Override
	public OutputStream writeByteStream()
		throws IOException
	{
		return writeByteStream(4096);
	}

	@Override
	public OutputStream writeByteStream(int chunkSize)
		throws IOException
	{
		ensure(1);
		buffer[index++] = (byte) (CborConstants.MAJOR_TYPE_BYTE_STRING << 5 | CborConstants.AI_INDEFINITE);

		return new CBORChunkOutputStream(chunkSize, (chunk, offset, len) -> {
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_BYTE_STRING, len);
			write(chunk, offset, len);
		})
		{
			@Override
			public void close()
				throws IOException
			{
				super.close();

				writeBreak();
			}
		};
	}

	@Override
	public void writeListStart()
		throws IOException
	{
		consumeWrite();

		increaseLevel(-1);

		ensure(1);
		buffer[index++] = (byte) (CborConstants.MAJOR_TYPE_ARRAY << 5 | CborConstants.AI_INDEFINITE);
	}

	@Override
	public void writeListStart(int items)
		throws IOException
	{
		consumeWrite();

		increaseLevel(items);
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_ARRAY, items);
	}

	@Override
	public void writeListEnd()
		throws IOException
	{
		if(decreaseLevel())
		{
			writeBreak();
		}
	}

	@Override
	public void writeObjectStart()
		throws IOException
	{
		consumeWrite();

		increaseLevel(-1);

		ensure(1);
		buffer[index++] = (byte) (CborConstants.MAJOR_TYPE_MAP << 5 | CborConstants.AI_INDEFINITE);
	}

	@Override
	public void writeObjectStart(int keyValuePairs)
		throws IOException
	{
		consumeWrite();

		increaseLevel(keyValuePairs * 2);
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_MAP, keyValuePairs);
	}

	@Override
	public void writeObjectEnd()
		throws IOException
	{
		if(decreaseLevel())
		{
			writeBreak();
		}
	}

	/**
	 * Write a tag to the output. This is provided as an extension method
	 * for serializers that support CBOR-specific encodings.
	 *
	 * @param tag
	 * @throws IOException
	 */
	public void writeTag(int tag)
		throws IOException
	{
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_TAGGED, tag);
	}

	private void writeMajorTypeAndLength(int majorType, int length)
		throws IOException
	{
		int symbol = majorType << 5;
		if(length < 24)
		{
			ensure(1);
			buffer[index++] = (byte) (symbol | length);
		}
		else if(length < 256)
		{
			ensure(2);
			buffer[index++] = (byte) (symbol | CborConstants.AI_ONE_BYTE);
			buffer[index++] = (byte) (length);
		}
		else if(length < 65536)
		{
			ensure(3);
			buffer[index++] = (byte) (symbol | CborConstants.AI_TWO_BYTES);
			buffer[index++] = (byte) ((length >> 8) & 0xff);
			buffer[index++] = (byte) (length & 0xff);
		}
		else
		{
			ensure(5);
			buffer[index++] = (byte) (symbol | CborConstants.AI_FOUR_BYTES);
			buffer[index++] = (byte) ((length >> 24) & 0xff);
			buffer[index++] = (byte) ((length >> 16) & 0xff);
			buffer[index++] = (byte) ((length >> 8) & 0xff);
			buffer[index++] = (byte) (length & 0xff);
		}
	}

	private void writeMajorTypeAndLength(int majorType, long length)
		throws IOException
	{
		int symbol = majorType << 5;
		if(length < 65536)
		{
			writeMajorTypeAndLength(majorType, (int) length);
		}
		else if(length < 4294967296l)
		{
			ensure(5);
			buffer[index++] = (byte) (symbol | CborConstants.AI_FOUR_BYTES);
			buffer[index++] = (byte) ((length >> 24) & 0xff);
			buffer[index++] = (byte) ((length >> 16) & 0xff);
			buffer[index++] = (byte) ((length >> 8) & 0xff);
			buffer[index++] = (byte) (length & 0xff);
		}
		else
		{
			ensure(9);
			buffer[index++] = (byte) (symbol | CborConstants.AI_EIGHT_BYTES);
			buffer[index++] = (byte) ((length >> 56) & 0xff);
			buffer[index++] = (byte) ((length >> 48) & 0xff);
			buffer[index++] = (byte) ((length >> 40) & 0xff);
			buffer[index++] = (byte) ((length >> 32) & 0xff);
			buffer[index++] = (byte) ((length >> 24) & 0xff);
			buffer[index++] = (byte) ((length >> 16) & 0xff);
			buffer[index++] = (byte) ((length >> 8) & 0xff);
			buffer[index++] = (byte) (length & 0xff);
		}
	}

	/**
	 * Consume a write verifying that there are writes still available if this
	 * a fixed-size map or array.
	 *
	 * @throws IOException
	 */
	private void consumeWrite()
		throws IOException
	{
		int remaining = remainingWrites[level];
		if(remaining == -1) return;

		if(remaining == 0)
		{
			throw new IOException("Tried writing an item, but would overflow fixed length of list or object");
		}

		remainingWrites[level] = remaining - 1;
	}

	/**
	 * Increase the output level.
	 *
	 * @param expectedLength
	 *   the number of items a list or object are expected to write, or {@code -1}
	 *   for indeterminate length
	 */
	private void increaseLevel(int expectedLength)
	{
		level++;
		if(remainingWrites.length == level)
		{
			// Grow lists when needed
			remainingWrites = Arrays.copyOf(remainingWrites, remainingWrites.length * 2);
		}

		remainingWrites[level] = expectedLength;
	}

	/**
	 * Decrease the level by one.
	 *
	 * @throws IOException
	 *   if not all items have been written
	 */
	private boolean decreaseLevel()
		throws IOException
	{
		int remaining = remainingWrites[level];
		if(remaining > 0)
		{
			throw new IOException("Not all items of the list or object have been written, remaining writes: " + remaining);
		}

		level--;
		return remaining == -1;
	}

	/**
	 * Write a break after an indeterminate map, list, byte string or text
	 * string.
	 *
	 * @throws IOException
	 */
	private void writeBreak()
		throws IOException
	{
		ensure(1);
		buffer[index++] = (byte) (CborConstants.MAJOR_TYPE_SIMPLE << 5 | CborConstants.SIMPLE_TYPE_BREAK);
	}

	private void ensure(int numberOfBytes)
		throws IOException
	{
		if(buffer.length > index + numberOfBytes)
		{
			return;
		}

		// TODO: Do we want to automatically flush?
		if(index > 512)
		{
			flushBuffer();
		}

		int length = buffer.length;
		if(length < numberOfBytes)
		{
			length = numberOfBytes;
		}
		buffer = Arrays.copyOf(buffer, buffer.length + length);
	}

	private void flushBuffer()
		throws IOException
	{
		if(index == 0) return;

		out.write(buffer, 0, index);
		index = 0;
	}

	private void write(byte[] data, int offset, int length)
		throws IOException
	{
		ensure(length);
		System.arraycopy(data, offset, buffer, index, length);
		index += length;
	}
}
