package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerFormatDefinition;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;
import se.l4.exobytes.format.ValueType;

/**
 * Serializer for {@link Boolean}.
 */
public class ByteSerializer
	implements Serializer<Byte>
{
	private final SerializerFormatDefinition formatDefinition;

	public ByteSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.BYTE);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "byte"));
	}

	@Override
	public Byte read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readByte();
	}

	@Override
	public void write(Byte object, StreamingOutput stream)
		throws IOException
	{
		stream.writeByte(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
