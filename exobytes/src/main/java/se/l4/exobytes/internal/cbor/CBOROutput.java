package se.l4.exobytes.internal.cbor;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import se.l4.commons.io.ChunkOutputStream;
import se.l4.exobytes.streaming.StreamingOutput;

/**
 * {@link StreamingOutput} that writes data in a CBOR format.
 */
public class CBOROutput
	implements StreamingOutput
{
	private static final int LEVELS = 20;

	private final OutputStream out;

	/**
	 * If the current level is using an indeterminate length and needs a break.
	 */
	private int[] remainingWrites;
	private int level;

	public CBOROutput(OutputStream out)
	{
		this.out = out;

		remainingWrites = new int[LEVELS];
		remainingWrites[0] = -1;
	}

	@Override
	public void close()
		throws IOException
	{
		out.close();
	}

	@Override
	public void flush()
		throws IOException
	{
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

		out.write((CborConstants.MAJOR_TYPE_SIMPLE << 5) | (b ? CborConstants.SIMPLE_TYPE_TRUE : CborConstants.SIMPLE_TYPE_FALSE));
	}

	@Override
	public void writeNull()
		throws IOException
	{
		consumeWrite();

		out.write((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_NULL);
	}

	@Override
	public void writeFloat(float number)
		throws IOException
	{
		consumeWrite();

		int bits = Float.floatToRawIntBits(number);
		out.write((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_FLOAT);
		out.write((bits >> 24) & 0xff);
		out.write((bits >> 16) & 0xff);
		out.write((bits >> 8) & 0xff);
		out.write(bits & 0xff);
	}

	@Override
	public void writeDouble(double number)
		throws IOException
	{
		consumeWrite();

		long bits = Double.doubleToRawLongBits(number);
		out.write((CborConstants.MAJOR_TYPE_SIMPLE << 5) | CborConstants.SIMPLE_TYPE_DOUBLE);
		out.write((byte) ((bits >> 56) & 0xff));
		out.write((byte) ((bits >> 48) & 0xff));
		out.write((byte) ((bits >> 40) & 0xff));
		out.write((byte) ((bits >> 32) & 0xff));
		out.write((byte) ((bits >> 24) & 0xff));
		out.write((byte) ((bits >> 16) & 0xff));
		out.write((byte) ((bits >> 8) & 0xff));
		out.write((byte) (bits & 0xff));
	}

	@Override
	public void writeString(String value)
		throws IOException
	{
		consumeWrite();

		// TODO: Optimization with better algorithm and chunking
		byte[] data = value.getBytes(StandardCharsets.UTF_8);
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_TEXT_STRING, data.length);
		out.write(data);
	}

	@Override
	public void writeBytes(byte[] data)
		throws IOException
	{
		writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_BYTE_STRING, data.length);
		out.write(data);
	}

	@Override
	public OutputStream writeBytes()
		throws IOException
	{
		return writeBytes(4096);
	}

	@Override
	public OutputStream writeBytes(int chunkSize)
		throws IOException
	{
		out.write(CborConstants.MAJOR_TYPE_BYTE_STRING << 5 | CborConstants.AI_INDEFINITE);
		return new ChunkOutputStream(chunkSize, (chunk, offset, len) -> {
			writeMajorTypeAndLength(CborConstants.MAJOR_TYPE_BYTE_STRING, len);
			out.write(chunk, offset, len);
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
		out.write(CborConstants.MAJOR_TYPE_ARRAY << 5 | CborConstants.AI_INDEFINITE);
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
		out.write(CborConstants.MAJOR_TYPE_MAP << 5 | CborConstants.AI_INDEFINITE);
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
			out.write(symbol | length);
		}
		else if(length < 256)
		{
			out.write(symbol | CborConstants.AI_ONE_BYTE);
			out.write(length);
		}
		else if(length < 65536)
		{
			out.write(symbol | CborConstants.AI_TWO_BYTES);
			out.write((length >> 8) & 0xff);
			out.write(length & 0xff);
		}
		else
		{
			out.write(symbol | CborConstants.AI_FOUR_BYTES);
			out.write((length >> 24) & 0xff);
			out.write((length >> 16) & 0xff);
			out.write((length >> 8) & 0xff);
			out.write(length & 0xff);
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
			out.write(symbol | CborConstants.AI_FOUR_BYTES);
			out.write((byte) ((length >> 24) & 0xff));
			out.write((byte) ((length >> 16) & 0xff));
			out.write((byte) ((length >> 8) & 0xff));
			out.write((byte) (length & 0xff));
		}
		else
		{
			out.write(symbol | CborConstants.AI_EIGHT_BYTES);
			out.write((byte) ((length >> 56) & 0xff));
			out.write((byte) ((length >> 48) & 0xff));
			out.write((byte) ((length >> 40) & 0xff));
			out.write((byte) ((length >> 32) & 0xff));
			out.write((byte) ((length >> 24) & 0xff));
			out.write((byte) ((length >> 16) & 0xff));
			out.write((byte) ((length >> 8) & 0xff));
			out.write((byte) (length & 0xff));
		}
	}

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

	private void writeBreak()
		throws IOException
	{
		out.write(CborConstants.MAJOR_TYPE_SIMPLE << 5 | CborConstants.SIMPLE_TYPE_BREAK);
	}
}
