package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link Integer}.
 */
public class IntSerializer
	implements Serializer<Integer>
{
	public IntSerializer()
	{
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "integer"));
	}

	@Override
	public Integer read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readInt();
	}

	@Override
	public void write(Integer object, StreamingOutput stream)
		throws IOException
	{
		stream.writeInt(object);
	}
}
