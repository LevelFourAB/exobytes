package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link String}.
 */
public class StringSerializer
	implements Serializer<String>
{
	public StringSerializer()
	{
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "string"));
	}

	@Override
	public String read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readString();
	}

	@Override
	public void write(String object, StreamingOutput stream)
		throws IOException
	{
		stream.writeString(object);
	}
}
