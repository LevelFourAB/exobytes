package se.l4.exobytes.streaming;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.OptionalInt;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.exobytes.Serializer;

/**
 * Input that is streamed as a set of token with values.
 */
public interface StreamingInput
	extends Closeable
{
	/**
	 * Peek into the stream and return the next token. Peeking is only possible
	 * if the value of {@link #current() current token} has been consumed.
	 *
	 * Tokens are considered consumed if:
	 * <ul>
	 *   <li>{@link Token#VALUE} and {@link Token#KEY} it has been read or {@link #skip() skipped}
	 *   <li>{@link Token#NULL} is always fully consumed
	 *   <li>
	 *     {@link Token#OBJECT_START}, {@link Token#OBJECT_END},
	 *     {@link Token#LIST_START} and {@link Token#LIST_END} have no values
	 *     and are always considered consumed
	 *   <li>{@link Token#END_OF_STREAM} is never consumed
	 * </ul>
	 *
	 * @return
	 * @throws IOException
	 *   if unable to read from the stream, or if the current token has not
	 *   been fully consumed
	 */
	@NonNull
	Token peek()
		throws IOException;

	/**
	 * Advance to the next token. If the current value has not been consumed
	 * this will automatically consume it.
	 *
	 * @return
	 * @throws IOException
	 */
	@NonNull
	Token next()
		throws IOException;

	/**
	 * Advance to the next token checking that it is of a certain type.
	 *
	 * @param expected
	 * @return
	 * @throws IOException
	 */
	@NonNull
	Token next(Token expected)
		throws IOException;

	/**
	 * Get the current token.
	 *
	 * @return
	 */
	@NonNull
	Token current();

	/**
	 * Get the length of the current list or object.
	 *
	 * @return
	 */
	@NonNull
	OptionalInt getLength();

	/**
	 * Skip the current token taking into account if it's an object or a list
	 * and in that case consuming everything until the object or list ends.
	 *
	 * @throws IOException
	 */
	void skip()
		throws IOException;

	/**
	 * Advance to the next token and then skip the value. This will call
	 * {@link #next()} followed by {@link #skip()}. The use case here is most
	 * commonly when the {@link #current() current token} is {@link Token#KEY}
	 * and a serializer wishes to skip value that follows.
	 *
	 * @throws IOException
	 */
	default void skipNext()
		throws IOException
	{
		next();
		skip();
	}

	/**
	 * Read any value from the input. The types returned by this method will
	 * be input specific and will not perform any conversions.
	 *
	 * @return
	 * @throws IOException
	 */
	Object readDynamic()
		throws IOException;

	/**
	 * Get the current value as a string.
	 *
	 * @return
	 */
	String readString()
		throws IOException;

	/**
	 * Get the value as a boolean.
	 *
	 * @return
	 */
	boolean readBoolean()
		throws IOException;

	/**
	 * Get the value as a byte.
	 *
	 * @return
	 */
	byte readByte()
		throws IOException;

	/**
	 * Get the value as a character.
	 *
	 * @return
	 */
	char readChar()
		throws IOException;

	/**
	 * Get the value as a double.
	 *
	 * @return
	 */
	double readDouble()
		throws IOException;

	/**
	 * Get the value as a float.
	 *
	 * @return
	 */
	float readFloat()
		throws IOException;

	/**
	 * Get the value as a long.
	 *
	 * @return
	 */
	long readLong()
		throws IOException;

	/**
	 * Get the value as an integer.
	 *
	 * @return
	 */
	int readInt()
		throws IOException;

	/**
	 * Get the value as a short.
	 *
	 * @return
	 */
	short readShort()
		throws IOException;

	/**
	 * Get the value as a byte[] array.
	 *
	 * @return
	 */
	byte[] readByteArray()
		throws IOException;

	/**
	 * Get the current binary value as an {@link InputStream}. This stream is
	 * only valid for the {@link #current() current token} and must be closed to
	 * allow this input to mark the value as consumed.
	 *
	 * @return
	 */
	InputStream readByteStream()
		throws IOException;

	/**
	 * Get the current binary value and copy it into the given output stream.
	 *
	 * @param out
	 * @throws IOException
	 */
	default long readByteStreamInto(OutputStream out)
		throws IOException
	{
		try(InputStream in = readByteStream())
		{
			return in.transferTo(out);
		}
	}

	/**
	 * Read an object using the given serializer.
	 */
	default <T> T readObject(Serializer<T> serializer)
		throws IOException
	{
		if(peek() != Token.NULL || serializer instanceof Serializer.NullHandling)
		{
			return serializer.read(this);
		}
		else
		{
			next(Token.NULL);
			return null;
		}
	}
}
