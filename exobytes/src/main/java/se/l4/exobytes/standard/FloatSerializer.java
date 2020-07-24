package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link Float}.
 */
public class FloatSerializer
	implements Serializer<Float>
{
	public FloatSerializer()
	{
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "float"));
	}

	@Override
	public Float read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readFloat();
	}

	@Override
	public void write(Float object, StreamingOutput stream)
		throws IOException
	{
		stream.writeFloat(object);
	}
}
