package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link Double}.
 */
public final class DoubleSerializer
	implements Serializer<Double>
{
	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "double"));
	}

	@Override
	public Double read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readDouble();
	}

	@Override
	public void write(Double object, StreamingOutput stream)
		throws IOException
	{
		stream.writeDouble(object);
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
