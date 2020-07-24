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
 * Serializer for {@link Short}.
 */
public class ShortSerializer
	implements Serializer<Short>
{
	private final SerializerFormatDefinition formatDefinition;

	public ShortSerializer()
	{
		formatDefinition = SerializerFormatDefinition.forValue(ValueType.SHORT);
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "short"));
	}

	@Override
	public Short read(StreamingInput in)
		throws IOException
	{
		in.next(Token.VALUE);
		return in.readShort();
	}

	@Override
	public void write(Short object, StreamingOutput stream)
		throws IOException
	{
		stream.writeInt(object);
	}

	@Override
	public SerializerFormatDefinition getFormatDefinition()
	{
		return formatDefinition;
	}
}
