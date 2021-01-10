package se.l4.exobytes.streaming;

/**
 * Tokens that the input can return.
 */
public enum Token
{
	/**
	 * Unknown token, reading has not started yet.
	 */
	UNKNOWN,
	/**
	 * Start of a list.
	 */
	LIST_START,
	/**
	 * End of a list.
	 */
	LIST_END,
	/**
	 * Start of an object.
	 */
	OBJECT_START,
	/**
	 * End of an object.
	 */
	OBJECT_END,
	/**
	 * Simple value.
	 */
	VALUE,
	/**
	 * Null, special case of {@link #VALUE}.
	 */
	NULL,
	/**
	 * Special token returned when end of stream has been reached.
	 */
	END_OF_STREAM
}
