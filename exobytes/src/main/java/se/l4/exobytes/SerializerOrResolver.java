package se.l4.exobytes;

/**
 * Either a {@link Serializer} or a {@link SerializerResolver}. Used to support
 * both picking a specific serializer and to resolve one when using {@link Use}
 * on classes.
 */
public interface SerializerOrResolver<T>
{

}
