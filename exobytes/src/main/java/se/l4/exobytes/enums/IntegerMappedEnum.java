package se.l4.exobytes.enums;

/**
 * Interface used to mark {@link Enum}s that can be mapped to and from an int, used together with
 * {@link IntegerMappedTranslator}.
 *
 */
public interface IntegerMappedEnum
{
	/**
	 * Get the value that is enum should mapped to.
	 *
	 * @return
	 */
	int getMappedValue();
}
