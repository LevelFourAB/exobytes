package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer for {@link Character}.
 */
public final class CharacterSerializer
	implements Serializer<Character>
{
	public static final Serializer<Character> INSTANCE = new CharacterSerializer();

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "char"));
	}

	@Override
	public Character read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readChar();
	}

	@Override
	public void write(Character object, StreamingOutput stream)
		throws IOException
	{
		stream.writeChar(object);
	}

	@Override
	public int hashCode()
	{
		return getClass().hashCode();
	}

	@Override
	public boolean equals(Object obj)
	{
		return obj != null && (this == obj || getClass() == obj.getClass());
	}

	@Override
	public String toString()
	{
		return getClass().getSimpleName() + "{}";
	}
}
