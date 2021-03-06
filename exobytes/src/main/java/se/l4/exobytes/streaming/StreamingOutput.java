package se.l4.exobytes.streaming;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import se.l4.exobytes.Serializer;

/**
 * Adapter for streaming results in different output formats.
 */
public interface StreamingOutput
	extends Flushable, Closeable
{
	/**
	 * Write the start of an object.
	 */
	void writeObjectStart()
		throws IOException;

	/**
	 * Write the start of an object.
	 *
	 * @param keyValuePairs
	 *   the number of key value pairs that will be written
	 */
	default void writeObjectStart(int keyValuePairs)
		throws IOException
	{
		writeObjectStart();
	}

	/**
	 * Write the end of an object.
	 *
	 * @throws IOException
	 */
	void writeObjectEnd()
		throws IOException;

	/**
	 * Write the start of a list.
	 *
	 * @throws IOException
	 */
	void writeListStart()
		throws IOException;

	/**
	 * Write the start of a list.
	 *
	 * @param items
	 *   the number of items that will be written
	 */
	default void writeListStart(int items)
		throws IOException
	{
		writeListStart();
	}

	/**
	 * Write the end of a list.
	 *
	 * @throws IOException
	 */
	void writeListEnd()
		throws IOException;

	/**
	 * Write a string.
	 *
	 * @param value
	 * @throws IOException
	 */
	void writeString(String value)
		throws IOException;

	/**
	 * Write a single byte value to the output.
	 *
	 * @param b
	 * @throws IOException
	 */
	void writeByte(byte b)
		throws IOException;

	/**
	 * Write a single char value to the output.
	 *
	 * @param c
	 * @throws IOException
	 */
	void writeChar(char c)
		throws IOException;

	/**
	 * Write a short to the output.
	 *
	 * @param s
	 * @throws IOException
	 */
	void writeShort(short s)
		throws IOException;

	/**
	 * Write an integer.
	 *
	 * @param number
	 * @throws IOException
	 */
	void writeInt(int number)
		throws IOException;

	/**
	 * Write a long.
	 *
	 * @param number
	 * @throws IOException
	 */
	void writeLong(long number)
		throws IOException;

	/**
	 * Write a float.
	 *
	 * @param number
	 * @throws IOException
	 */
	void writeFloat(float number)
		throws IOException;

	/**
	 * Write a double.
	 *
	 * @param number
	 * @throws IOException
	 */
	void writeDouble(double number)
		throws IOException;

	/**
	 * Write a boolean.
	 *
	 * @param b
	 * @throws IOException
	 */
	void writeBoolean(boolean b)
		throws IOException;

	/**
	 * Write a byte array to the output.
	 *
	 * @param data
	 * @throws IOException
	 */
	void writeByteArray(byte[] data)
		throws IOException;

	/**
	 * Get an output stream that can be used to write binary data to this
	 * output. The output must be closed.
	 *
	 * @return
	 *   stream that can be used to write data to this output
	 * @throws IOException
	 */
	OutputStream writeByteStream()
		throws IOException;

	/**
	 * Get an output stream that can be used to write binary data to this
	 * output. The output must be closed.
	 *
	 * @param chunkSize
	 *   control the size of the chunks if this output supports them
	 * @return
	 *   stream that can be used to write data to this output
	 * @throws IOException
	 */
	default OutputStream writeByteStream(int chunkSize)
		throws IOException
	{
		return writeByteStream();
	}

	/**
	 * Copy data from an {@link InputStream} to this output. Works using
	 * {@link #writeByteStream()} and copying data.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	default long writeByteStream(InputStream in)
		throws IOException
	{
		try(OutputStream out = writeByteStream())
		{
			return in.transferTo(out);
		}
	}

	/**
	 * Copy data from an {@link InputStream} to this output. Works using
	 * {@link #writeByteStream()} and copying data.
	 *
	 * @param in
	 * @param chunkSize
	 *   control the size of the chunks if this output supports them
	 * @return
	 *   the number of bytes written
	 * @throws IOException
	 */
	default long writeByteStream(InputStream in, int chunkSize)
		throws IOException
	{
		try(OutputStream out = writeByteStream(chunkSize))
		{
			return in.transferTo(out);
		}
	}

	/**
	 * Write a null value.
	 *
	 * @throws IOException
	 */
	void writeNull()
		throws IOException;

	/**
	 * Write an object to the output.
	 *
	 * @param <T>
	 * @param serializer
	 * @param object
	 * @throws IOException
	 */
	@SuppressFBWarnings("NP_NONNULL_PARAM_VIOLATION")
	default <T> void writeObject(Serializer<T> serializer, T object)
		throws IOException
	{
		if(object == null && ! (serializer instanceof Serializer.NullHandling))
		{
			writeNull();
		}
		else
		{
			serializer.write(object, this);
		}
	}

	/**
	 * Write dynamic data to this output.
	 *
	 * @param data
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked" })
	default void writeDynamic(Object data)
		throws IOException
	{
		if(data == null)
		{
			writeNull();
		}
		else if(data instanceof Map)
		{
			Map<Object, Object> asMap = (Map<Object, Object>) data;
			writeObjectStart();
			for(Map.Entry<Object, Object> e : asMap.entrySet())
			{
				writeDynamic(e.getKey());
				writeDynamic(e.getValue());
			}
			writeObjectEnd();
		}
		else if(data instanceof Iterable)
		{
			Iterable<Object> asIterable = (Iterable<Object>) data;
			writeListStart();
			for(Object o : asIterable)
			{
				writeDynamic(o);
			}
			writeListEnd();
		}
		else if(data instanceof String)
		{
			writeString((String) data);
		}
		else if(data instanceof Boolean)
		{
			writeBoolean((boolean) data);
		}
		else if(data instanceof Byte)
		{
			writeByte((byte) data);
		}
		else if(data instanceof Short)
		{
			writeShort((short) data);
		}
		else if(data instanceof Character)
		{
			writeChar((char) data);
		}
		else if(data instanceof Integer)
		{
			writeInt((int) data);
		}
		else if(data instanceof Long)
		{
			writeLong((long) data);
		}
		else if(data instanceof Float)
		{
			writeFloat((float) data);
		}
		else if(data instanceof Double)
		{
			writeDouble((double) data);
		}
		else if(data instanceof Number)
		{
			writeDouble(((Number) data).doubleValue());
		}
		else if(data instanceof byte[])
		{
			writeByteArray((byte[]) data);
		}
		else
		{
			throw new IOException("Unsupported data of type " + data.getClass() + ", value: " + data);
		}
	}
}
