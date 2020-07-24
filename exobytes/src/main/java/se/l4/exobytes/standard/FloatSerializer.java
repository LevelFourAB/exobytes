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
public final class FloatSerializer
	implements Serializer<Float>
{
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
