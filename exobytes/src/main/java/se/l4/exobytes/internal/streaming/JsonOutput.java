package se.l4.exobytes.internal.streaming;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import se.l4.exobytes.streaming.StreamingOutput;

/**
 * Streamer that outputs JSON.
 *
 */
public class JsonOutput
	implements StreamingOutput
{
	private static final int HEX_MASK = (1 << 4) - 1;

	private static final byte[] DIGITS = {
		'0', '1', '2', '3', '4', '5', '6', '7',
		'8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
	};

	private final static byte[] BASE64 = {
		'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
		'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
		'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
		'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'
	};

	private static final int LEVELS = 20;

	protected final OutputStream out1;
	private final boolean beautify;

	private byte[] buffer;
	private int index;

	private boolean[] lists;
	private boolean[] hasData;

	private int level;
	private boolean nextKey;

	/**
	 * Create a JSON streamer that will write to the given output.
	 *
	 * @param out
	 */
	public JsonOutput(OutputStream out)
	{
		this(out, false);
	}

	/**
	 * Create a JSON streamer that will write to the given output, optionally
	 * with beautification of the generated JSON.
	 *
	 * @param out
	 * @param beautify
	 */
	public JsonOutput(OutputStream out, boolean beautify)
	{
		this.out1 = out;
		this.beautify = beautify;

		this.buffer = new byte[32];

		lists = new boolean[LEVELS];
		hasData = new boolean[LEVELS];
	}

	@Override
	public void close()
		throws IOException
	{
		flushBuffer();
		out1.close();
	}

	@Override
	public void flush()
		throws IOException
	{
		flushBuffer();
		out1.flush();
	}

	/**
	 * Increase the level by one.
	 *
	 * @param list
	 */
	private void increaseLevel(boolean list)
	{
		level++;

		if(hasData.length == level)
		{
			// Grow lists when needed
			hasData = Arrays.copyOf(hasData, hasData.length * 2);
			lists = Arrays.copyOf(lists, hasData.length * 2);
		}

		hasData[level] = false;
		lists[level] = list;

		nextKey = ! list;
	}

	/**
	 * Decrease the level by one.
	 *
	 * @throws IOException
	 */
	private void decreaseLevel()
		throws IOException
	{
		level--;
		nextKey = ! lists[level];

		if(beautify && hasData[level])
		{
			ensure(level);
			buffer[index++] = '\n';

			for(int i=0; i<level; i++)
			{
				buffer[index++] = '\t';
			}
		}
	}

	/**
	 * Helper to check if this write is a key and if so fail it as this output
	 * only supports string keys.
	 */
	private void failKey()
		throws IOException
	{
		if(nextKey)
		{
			throw new IOException("Trying to write a key that is not a string");
		}

		// If we are reading an object make sure there is a key
		nextKey = ! lists[level];
	}

	/**
	 * Start a write, will output commas and beautification if needed.
	 *
	 * @throws IOException
	 */
	private void startWrite()
		throws IOException
	{
		if(! lists[level] && ! nextKey)
		{
			return;
		}

		if(hasData[level])
		{
			ensure(1);
			buffer[index++] = ',';
		}

		hasData[level] = true;

		if(beautify && level > 0)
		{
			ensure(level);
			buffer[index++] = '\n';

			for(int i=0; i<level; i++)
			{
				buffer[index++] = '\t';
			}
		}
	}

	@Override
	public void writeObjectStart()
		throws IOException
	{
		startWrite();
		failKey();

		ensure(1);
		buffer[index++] = '{';

		increaseLevel(false);
	}

	@Override
	public void writeObjectEnd()
		throws IOException
	{
		if(! nextKey)
		{
			throw new IOException("Trying to end an object without writing a value");
		}

		decreaseLevel();
		ensure(1);
		buffer[index++] = '}';
	}

	@Override
	public void writeListStart()
		throws IOException
	{
		startWrite();
		failKey();

		ensure(1);
		buffer[index++] = '[';

		increaseLevel(true);
	}

	@Override
	public void writeListEnd()
		throws IOException
	{
		failKey();
		decreaseLevel();

		ensure(1);
		buffer[index++] = ']';
	}

	@Override
	public void writeString(String value)
		throws IOException
	{
		startWrite();

		ensure(1);
		buffer[index++] = '"';

		for(int i=0, n=value.length(); i<n; i++)
		{
			char c = value.charAt(i);
			switch(c)
			{
				case '"':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = '"';
					break;
				case '\\':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = '\\';
					break;
				case '\r':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = 'r';
					break;
				case '\n':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = 'n';
					break;
				case '\t':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = 't';
					break;
				case '\b':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = 'b';
					break;
				case '\f':
					ensure(2);
					buffer[index++] = '\\';
					buffer[index++] = 'f';
					break;
				default:
					if(c <= 0x1F)
					{
						ensure(6);
						buffer[index++] = '\\';
						buffer[index++] = 'u';
						buffer[index++] = '0';
						buffer[index++] = '0';
						buffer[index++] = DIGITS[(c >> 4) & HEX_MASK];
						buffer[index++] = DIGITS[c & HEX_MASK];
					}
					else if(c >= 127)
					{
						ensure(6);
						buffer[index++] = '\\';
						buffer[index++] = 'u';
						buffer[index++] = DIGITS[(c >> 12) & HEX_MASK];
						buffer[index++] = DIGITS[(c >> 8) & HEX_MASK];
						buffer[index++] = DIGITS[(c >> 4) & HEX_MASK];
						buffer[index++] = DIGITS[c & HEX_MASK];
					}
					else
					{
						ensure(1);
						buffer[index++] = (byte) c;
					}
			}
		}

		ensure(1);
		buffer[index++] = '"';

		if(nextKey)
		{
			nextKey = false;
			ensure(1);
			buffer[index++] = ':';
		}
		else
		{
			nextKey = ! lists[level];
		}
	}

	private void writeUnescaped(String value)
		throws IOException
	{
		startWrite();
		failKey();

		ensure(value.length());
		for(int i=0, n=value.length(); i<n; i++)
		{
			buffer[index++] = (byte) value.charAt(i);
		}
	}

	@Override
	public void writeByte(byte b)
		throws IOException
	{
		writeUnescaped(Byte.toString(b));
	}

	@Override
	public void writeChar(char c)
		throws IOException
	{
		writeString(String.valueOf(c));
	}

	@Override
	public void writeShort(short s)
		throws IOException
	{
		writeUnescaped(Short.toString(s));
	}

	@Override
	public void writeInt(int number)
		throws IOException
	{
		writeUnescaped(Integer.toString(number));
	}

	@Override
	public void writeLong(long number)
		throws IOException
	{
		writeUnescaped(Long.toString(number));
	}

	@Override
	public void writeFloat(float number)
		throws IOException
	{
		writeUnescaped(Float.toString(number));
	}

	@Override
	public void writeDouble(double number)
		throws IOException
	{
		writeUnescaped(Double.toString(number));
	}

	@Override
	public void writeBoolean(boolean bool)
		throws IOException
	{
		startWrite();
		failKey();

		if(bool)
		{
			ensure(4);
			buffer[index++] = 't';
			buffer[index++] = 'r';
			buffer[index++] = 'u';
			buffer[index++] = 'e';
		}
		else
		{
			ensure(5);
			buffer[index++] = 'f';
			buffer[index++] = 'a';
			buffer[index++] = 'l';
			buffer[index++] = 's';
			buffer[index++] = 'e';
		}
	}

	@Override
	public void writeByteArray(byte[] data)
		throws IOException
	{
		startWrite();
		failKey();

		if(data == null)
		{
			writeRawNull();
			return;
		}

		ensure(1);
		buffer[index++] = '"';

		int i = 0;
		for(int n=data.length - 2; i<n; i+=3)
		{
			write(data, i, 3);
		}

		if(i < data.length)
		{
			write(data, i, data.length - i);
		}

		ensure(1);
		buffer[index++] = '"';
	}

	@Override
	public OutputStream writeByteStream()
		throws IOException
	{
		return new ByteArrayOutputStream()
		{
			@Override
			public void close()
				throws IOException
			{
				writeByteArray(toByteArray());
			}
		};
	}

	/**
	 * Write some BASE64 encoded bytes.
	 *
	 * @param data
	 * @param pos
	 * @param chars
	 * @param len
	 * @throws IOException
	 */
	private void write(byte[] data, int pos, int len)
		throws IOException
	{
		byte[] chars = BASE64;

		int loc = (len > 0 ? (data[pos] << 24) >>> 8 : 0) |
			(len > 1 ? (data[pos+1] << 24) >>> 16 : 0) |
			(len > 2 ? (data[pos+2] << 24) >>> 24 : 0);

		ensure(4);
		switch(len)
		{
			case 3:
				buffer[index++] = chars[loc >>> 18];
				buffer[index++] =  chars[(loc >>> 12) & 0x3f];
				buffer[index++] =  chars[(loc >>> 6) & 0x3f];
				buffer[index++] =  chars[loc & 0x3f];
				break;
			case 2:
				buffer[index++] = chars[loc >>> 18];
				buffer[index++] =  chars[(loc >>> 12) & 0x3f];
				buffer[index++] =  chars[(loc >>> 6) & 0x3f];
				buffer[index++] = '=';
				break;
			case 1:
				buffer[index++] = chars[loc >>> 18];
				buffer[index++] = chars[(loc >>> 12) & 0x3f];
				buffer[index++] = '=';
				buffer[index++] =  '=';
		}
	}

	@Override
	public void writeNull()
		throws IOException
	{
		startWrite();
		failKey();

		writeRawNull();
	}

	private void writeRawNull()
		throws IOException
	{
		ensure(4);
		buffer[index++] = 'n';
		buffer[index++] = 'u';
		buffer[index++] = 'l';
		buffer[index++] = 'l';
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

		out1.write(buffer, 0, index);
		index = 0;
	}
}
