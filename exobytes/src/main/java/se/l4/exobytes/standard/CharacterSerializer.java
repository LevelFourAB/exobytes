package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link Character}.
 */
public class CharacterSerializer
	implements Serializer<Character>
{
	public CharacterSerializer()
	{
	}

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
}
