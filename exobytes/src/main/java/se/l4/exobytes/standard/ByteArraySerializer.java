package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for byte arrays as they have special meaning in
 * {@link StreamingInput} and {@link StreamingOutput}.
 */
public class ByteArraySerializer
	implements Serializer<byte[]>
{
	public ByteArraySerializer()
	{
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "byte[]"));
	}

	@Override
	public byte[] read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readByteArray();
	}

	@Override
	public void write(byte[] object, StreamingOutput stream)
		throws IOException
	{
		stream.writeBytes(object);
	}
}
