package se.l4.exobytes;

import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Resolver for a specific {@link Serializer}. This is used to support
 * generics and other semi-dynamic features.
 *
 * @param <T>
 */
public interface SerializerResolver<T>
	extends SerializerOrResolver<T>
{
	/**
	 * Attempt to find a suitable serializer.
	 *
	 * @param encounter
	 * @return
	 */
	@NonNull
	Optional<? extends SerializerOrResolver<T>> find(@NonNull TypeEncounter encounter);
}
