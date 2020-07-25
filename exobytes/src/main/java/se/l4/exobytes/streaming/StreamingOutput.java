package se.l4.exobytes.streaming;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.OutputStream;

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
	 * @param name
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
	 * @return
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
	 * @return
	 * @throws IOException
	 */
	void writeInt(int number)
		throws IOException;

	/**
	 * Write a long.
	 *
	 * @param number
	 * @return
	 * @throws IOException
	 */
	void writeLong(long number)
		throws IOException;

	/**
	 * Write a float.
	 *
	 * @param number
	 * @return
	 * @throws IOException
	 */
	void writeFloat(float number)
		throws IOException;

	/**
	 * Write a double.
	 *
	 * @param number
	 * @return
	 * @throws IOException
	 */
	void writeDouble(double number)
		throws IOException;

	/**
	 * Write a boolean.
	 *
	 * @param b
	 * @return
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
	void writeBytes(byte[] data)
		throws IOException;

	/**
	 * Get an output stream that can be used to write binary data to this
	 * output. The output must be closed.
	 *
	 * @return
	 *   stream that can be used to write data to this output
	 * @throws IOException
	 */
	OutputStream writeBytes()
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
	default OutputStream writeBytes(int chunkSize)
		throws IOException
	{
		return writeBytes();
	}

	/**
	 * Write a null value.
	 *
	 * @return
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
}
