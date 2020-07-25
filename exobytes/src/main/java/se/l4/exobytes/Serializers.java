package se.l4.exobytes;

import java.lang.annotation.Annotation;
import java.util.Optional;

import edu.umd.cs.findbugs.annotations.NonNull;
import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.internal.SerializersBuilderImpl;


/**
 * Collection of {@link Serializer}s and {@link SerializerResolver resolvers}.
 *
 */
public interface Serializers
{
	/**
	 * Get the current instance factory.
	 *
	 * @return
	 */
	@NonNull
	InstanceFactory getInstanceFactory();

	/**
	 * Bind a certain type automatically discovering which serializer to
	 * use.
	 *
	 * @param type
	 */
	@NonNull
	Serializers register(@NonNull Class<?> type);

	/**
	 * Bind a given type to the specified serializer.
	 *
	 * @param <T>
	 * @param type
	 * @param serializer
	 */
	@NonNull
	<T> Serializers register(@NonNull Class<T> type, @NonNull Serializer<T> serializer);

	/**
	 * Bind a given type to the specified resolver. The resolver will be
	 * asked to resolve a more specific serializer based on type parameters.
	 *
	 * @param <T>
	 * @param type
	 * @param resolver
	 */
	@NonNull
	<T> Serializers register(@NonNull Class<T> type, @NonNull SerializerResolver<? extends T> resolver);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Serializer<T> get(@NonNull Class<T> type);

	/**
	 * Find a serializer suitable for the specific type.
	 *
	 * @param <T>
	 * @param type
	 * @return
	 */
	@NonNull
	<T> Serializer<T> get(@NonNull Class<T> type, @NonNull Iterable<? extends Annotation> hints);

	/**
	 * Find a serializer suitable for the specified type.
	 *
	 * @param type
	 * @return
	 */
	@NonNull
	Serializer<?> get(@NonNull TypeRef type);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> getViaName(String name);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> getViaName(QualifiedName name);

	/**
	 * Find a serializer based on its registered name.
	 *
	 * @param namespace
	 * @param name
	 * @return
	 */
	@NonNull
	Optional<? extends Serializer<?>> getViaName(@NonNull String namespace, @NonNull String name);

	/**
	 * Get if the given type can be serialized.
	 *
	 * @param type
	 * @return
	 */
	boolean isSupported(@NonNull Class<?> type);

	/**
	 * Start building a new instance of {@link Serializers}.
	 *
	 * @return
	 */
	static Builder create()
	{
		return new SerializersBuilderImpl();
	}

	/**
	 * Builder for an instance of {@link Serializers}.
	 */
	interface Builder
	{
		/**
		 * Set the instance factory to use.
		 *
		 * @param factory
		 * @return
		 */
		Builder withInstanceFactory(InstanceFactory factory);

		/**
		 * Indicate that no default serializers should be registered. This
		 * will create a serializer that can serialize nothing.
		 *
		 * @return
		 */
		Builder empty();

		/**
		 * Add a module to the built instance.
		 *
		 * @param module
		 * @return
		 */
		Builder addModule(SerializersModule module);

		/**
		 * Create an instance that wraps another instance. This allows for
		 * registering serializers that are only available for use when that
		 * specific serializer is used.
		 *
		 * @param serializers
		 * @return
		 */
		Builder wrap(Serializers serializers);

		/**
		 * Build the instance.
		 *
		 * @return
		 */
		Serializers build();
	}
}
