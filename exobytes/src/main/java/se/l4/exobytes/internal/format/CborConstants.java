package se.l4.exobytes.internal.format;

/**
 * Constants used for CBOR.
 */
public interface CborConstants
{
	static int MAJOR_TYPE_UNSIGNED_INT = 0;
	static int MAJOR_TYPE_NEGATIVE_INT = 1;
	static int MAJOR_TYPE_BYTE_STRING = 2;
	static int MAJOR_TYPE_TEXT_STRING = 3;
	static int MAJOR_TYPE_ARRAY = 4;
	static int MAJOR_TYPE_MAP = 5;
	static int MAJOR_TYPE_TAGGED = 6;
	static int MAJOR_TYPE_SIMPLE = 7;

	static int AI_ONE_BYTE = 24;
	static int AI_TWO_BYTES = 25;
	static int AI_FOUR_BYTES = 26;
	static int AI_EIGHT_BYTES = 27;
	static int AI_INDEFINITE = 31;

	static int SIMPLE_TYPE_FALSE = 20;
	static int SIMPLE_TYPE_TRUE = 21;
	static int SIMPLE_TYPE_NULL = 22;

	static int SIMPLE_TYPE_HALF = 25;
	static int SIMPLE_TYPE_FLOAT = 26;
	static int SIMPLE_TYPE_DOUBLE = 27;

	static int SIMPLE_TYPE_BREAK = 31;
}
