package se.l4.exobytes.streaming;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import se.l4.exobytes.SerializationException;

/**
 * Abstract implementation of {@link StreamingInput} to simplify common
 * operations such as peeking and value setting.
 */
public abstract class AbstractStreamingInput
	implements StreamingInput
{
	protected Token current;
	protected boolean didReadValue;

	public AbstractStreamingInput()
	{
		current = Token.UNKNOWN;
	}

	@Override
	public Token peek()
		throws IOException
	{
		if(! didReadValue && current == Token.VALUE)
		{
			throw new IOException("Can not peek next token, current token is not fully consumed");
		}

		return peek0();
	}

	/**
	 * Peek the next token.
	 *
	 * @return
	 * @throws IOException
	 */
	protected abstract Token peek0()
		throws IOException;

	@Override
	public Token current()
	{
		return current;
	}

	@Override
	public void current(Token expected)
	{
		if(current != expected)
		{
			throw raiseSerializationException("Expected " + expected + " but currently " + current);
		}
	}

	@Override
	public Token next()
		throws IOException
	{
		if(current == Token.END_OF_STREAM)
		{
			throw new IOException("Tried reading past end of stream");
		}

		if(! didReadValue && current == Token.VALUE)
		{
			skipValue();
		}

		Token token = next0();
		didReadValue = false;
		return this.current = token;
	}

	/**
	 * Read the next token.
	 *
	 * @return
	 * @throws IOException
	 */
	protected abstract Token next0()
		throws IOException;

	protected IOException raiseException(String message)
	{
		return new IOException(message);
	}

	protected SerializationException raiseSerializationException(String message)
	{
		return new SerializationException(message);
	}

	@Override
	public Token next(Token expected)
		throws IOException
	{
		Token t = next();
		if(t != expected)
		{
			throw raiseSerializationException("Expected "+ expected + " but got " + t);
		}
		return t;
	}

	@Override
	public void skip()
		throws IOException
	{
		switch(current())
		{
			case LIST_START:
			case OBJECT_START:
				int depth = 1;

				while(depth != 0)
				{
					switch(next())
					{
						case OBJECT_START:
						case LIST_START:
							depth++;
							break;
						case OBJECT_END:
						case LIST_END:
							depth--;
							break;
						default:
							// Ignore - nothing special to do in this case
					}
				}
				break;
			case NULL:
				break;
			case VALUE:
				if(! didReadValue)
				{
					skipValue();
				}
				return;
			default:
				throw raiseException("Can only skip when start of object, start of list or value, token is now " + current);
		}
	}

	/**
	 * Skip the current null token.
	 *
	 * @throws IOException
	 */
	protected abstract void skipValue()
		throws IOException;

	@Override
	public Object readDynamic()
		throws IOException
	{
		switch(current)
		{
			case OBJECT_START:
				Map<Object, Object> map = new HashMap<>();

				while(peek() != Token.OBJECT_END)
				{
					next();
					Object key = readDynamic0();
					next();
					Object value = readDynamic();
					map.put(key, value);
				}

				next(Token.OBJECT_END);

				return map;
			case LIST_START:
				List<Object> list = new ArrayList<>();
				while(peek() != Token.LIST_END)
				{
					next();
					list.add(readDynamic());
				}
				return list;
			case NULL:
				return null;
			case VALUE:
				return readDynamic0();
			default:
				throw raiseException("Unable to read a value");
		}
	}

	/**
	 * Dynamically read a single value from the input.
	 *
	 * @return
	 * @throws IOException
	 */
	protected abstract Object readDynamic0()
		throws IOException;

	/**
	 * Mark the current value as read.
	 *
	 * @throws IOException
	 */
	protected void markValueRead()
		throws IOException
	{
		didReadValue = true;
	}
}
