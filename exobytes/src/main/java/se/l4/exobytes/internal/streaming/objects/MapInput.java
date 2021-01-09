package se.l4.exobytes.internal.streaming.objects;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.OptionalInt;

import se.l4.exobytes.streaming.AbstractStreamingInput;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.Token;

/**
 * Implementation of {@link StreamingInput} that works on a objects.
 */
public class MapInput
	extends AbstractStreamingInput
{
	private enum State
	{
		START,
		KEY,
		VALUE,
		END,
		DONE
	}

	private final Iterator<Map.Entry<Object, Object>> iterator;

	private Map.Entry<Object, Object> currentKey;

	private State state;
	private State previousState;

	private StreamingInput subInput;

	public MapInput(Map<Object, Object> map)
	{
		state = State.START;

		this.iterator = map.entrySet().iterator();
	}

	@Override
	public void close()
		throws IOException
	{
		// Nothing to close
	}

	public static StreamingInput resolveInput(Object value)
	{
		if(value == null)
		{
			return new NullInput();
		}
		else if(value instanceof Collection)
		{
			return new ListInput((Collection) value);
		}
		else if(value instanceof Map)
		{
			return new MapInput((Map) value);
		}
		else
		{
			return new ValueInput(value);
		}
	}

	@Override
	public Token peek0()
		throws IOException
	{
		switch(state)
		{
			case START:
				return Token.OBJECT_START;
			case KEY:
				return Token.VALUE;
			case VALUE:
				Token peeked = subInput.peek();
				if(peeked != Token.END_OF_STREAM)
				{
					return peeked;
				}
				else
				{
					advancePosition();
					return peek();
				}
			case END:
				return Token.OBJECT_END;
		}

		return Token.END_OF_STREAM;
	}

	@Override
	public Token next0()
		throws IOException
	{
		switch(state)
		{
			case START:
				// Check what the next state should be
				advancePosition();
				return Token.OBJECT_START;
			case KEY:
			{
				Token t = subInput.next();
				if(t == Token.END_OF_STREAM)
				{
					// Nothing left in the value, advance and check again
					setState(State.VALUE);
					subInput = MapInput.resolveInput(currentKey.getKey());
					return next();
				}
				return t;
			}
			case VALUE:
			{
				/*
				 * Value state, check the sub input until it returns null
				 */
				Token t = subInput.next();
				if(t == Token.END_OF_STREAM)
				{
					// Nothing left in the value, advance and check again
					advancePosition();
					return next();
				}

				setState(State.VALUE);
				return t;
			}
			case END:
				setState(State.DONE);
				return Token.OBJECT_END;
		}

		return Token.END_OF_STREAM;
	}

	private void setState(State state)
	{
		previousState = this.state;
		this.state = state;
	}

	private void advancePosition()
	{
		if(iterator.hasNext())
		{
			currentKey = iterator.next();
			setState(State.KEY);
			subInput = MapInput.resolveInput(currentKey.getKey());
		}
		else
		{
			setState(State.END);
		}
	}

	@Override
	protected void skipValue()
		throws IOException
	{
	}

	@Override
	public Token current()
	{
		return subInput != null ? subInput.current() : current;
	}

	@Override
	public OptionalInt getLength()
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.getLength();
			default:
				return OptionalInt.empty();
		}
	}

	@Override
	public Object readDynamic0()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readDynamic();
		}

		return null;
	}

	@Override
	public String readString()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readString();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public boolean readBoolean()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readBoolean();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public double readDouble()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readDouble();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public float readFloat()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readFloat();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public long readLong()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readLong();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public int readInt()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readInt();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public short readShort()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readShort();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public byte readByte()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readByte();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public char readChar()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readChar();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public byte[] readByteArray()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readByteArray();
			default:
				throw raiseException("Not reading a value");
		}
	}

	@Override
	public InputStream readByteStream()
		throws IOException
	{
		switch(previousState)
		{
			case KEY:
			case VALUE:
				return subInput.readByteStream();
			default:
				throw raiseException("Not reading a value");
		}
	}
}
