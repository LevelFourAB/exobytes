package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link UUID} that transforms into a byte array.
 */
public final class UuidSerializer
	implements Serializer<UUID>
{
	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.of(new QualifiedName("", "uuid"));
	}

	@Override
	public UUID read(StreamingInput in) throws IOException
	{
		in.next(Token.VALUE);
		return fromBytes0(in.readByteArray());
	}

	@Override
	public void write(UUID object, StreamingOutput stream)
		throws IOException
	{
		stream.writeBytes(toBytes0(object));
	}

	private static UUID fromBytes0(byte[] bytes)
	{
		if(bytes == null) return null;

		long msb = 0;
		long lsb = 0;
		for(int i=0; i<8; i++)
		{
			msb = (msb << 8) | (bytes[i] & 0xff);
			lsb = (lsb << 8) | (bytes[8 + i] & 0xff);
		}

		return new UUID(msb, lsb);
	}

	private static byte[] toBytes0(UUID uuid)
	{
		long msb = uuid.getMostSignificantBits();
		long lsb = uuid.getLeastSignificantBits();

		byte[] buffer = new byte[16];
		for(int i=0; i<8; i++)
		{
			buffer[i] = (byte) (msb >>> 8 * (7 - i));
			buffer[8+i] = (byte) (lsb >>> 8 * (7 - i));
		}

		return buffer;
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
