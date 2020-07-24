package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.format.Token;

/**
 * Serializer for {@link Optional} values. Will treat null as an empty
 * optional.
 */
public class OptionalSerializer<T>
	implements Serializer<Optional<T>>, Serializer.NullHandling
{
	private final Serializer<T> itemSerializer;

	public OptionalSerializer(Serializer<T> itemSerializer)
	{
		this.itemSerializer = itemSerializer;
	}

	@Override
	public Optional<T> read(StreamingInput in)
		throws IOException
	{
		if(in.peek() == Token.NULL)
		{
			// Consume the null value
			in.next();
			return Optional.empty();
		}

		T item = itemSerializer.read(in);
		return Optional.of(item);
	}

	@Override
	public void write(Optional<T> object, StreamingOutput stream)
		throws IOException
	{
		if(object != null && object.isPresent())
		{
			// Use the item serializer to serialize if there is an object present
			itemSerializer.write(object.get(), stream);
		}
		else
		{
			// If there is no object, write a null value
			stream.writeNull();
		}
	}
}
