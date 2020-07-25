package se.l4.exobytes;

/**
 * Module encapsulation for configuration of serializers for certain types.
 */
public interface SerializersModule
{
	/**
	 * Activate this module using the given {@link Serializers} instance.
	 */
	void activate(Serializers serializers);
}
