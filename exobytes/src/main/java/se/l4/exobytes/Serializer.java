package se.l4.exobytes;

import java.io.IOException;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import edu.umd.cs.findbugs.annotations.Nullable;
import edu.umd.cs.findbugs.annotations.UnknownNullness;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;

/**
 * Serializer for a specific class. A serializer is used to read and write
 * objects and is usually bound to a specific class. Serializers are retrieved
 * via a {@link Serializers}.
 *
 * @param <T>
 */
public interface Serializer<T>
	extends SerializerOrResolver<T>
{
	/**
	 * Read an object from the specified stream.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	@Nullable
	T read(@NonNull StreamingInput in)
		throws IOException;

	/**
	 * Write and object to the specified stream.
	 *
	 * @param object
	 *   object to write, if the serializer implements {@link NullHandling}
	 *   this may be {@code null}, if not the serializer can assume it is not
	 *   {@code null}
	 * @param out
	 * 	 the stream to use for writing
	 * @throws IOException
	 *   if unable to write the object
	 */
	void write(@UnknownNullness T object, @NonNull StreamingOutput out)
		throws IOException;

	/**
	 * Get the name of this serializer.
	 *
	 * @return
	 */
	default Optional<QualifiedName> getName()
	{
		return Optional.empty();
	}

	/**
	 * Marker interface used when a serializer wants to handle an incoming
	 * {@code null} value. If a serializer does not implement this interface
	 * {@code null} values are mapped to default values automatically by
	 * the reflection serializer.
	 */
	interface NullHandling
	{
	}
}
