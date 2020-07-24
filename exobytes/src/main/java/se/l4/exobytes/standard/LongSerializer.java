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
 * Serializer for {@link Long}.
 */
public class LongSerializer
	implements Serializer<Long>
{
	private final SerializerFormatDefinition formatDefinition;

	public LongSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.LONG);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "long"));
	}

	@Override
	public Long read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readLong();
	}

	@Override
	public void write(Long object, StreamingOutput stream)
		throws IOException
	{
		stream.writeLong(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
