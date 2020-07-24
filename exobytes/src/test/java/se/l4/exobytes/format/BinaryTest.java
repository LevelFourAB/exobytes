package se.l4.exobytes.format;

/**
 * Tests for the binary format. Tests by first writing some values and then
 * checking that it is possible to read the serialized stream.
 */
public class BinaryTest
	extends StreamingFormatTest
{
	@Override
	protected StreamingFormat format()
	{
		return StreamingFormat.BINARY;
	}
}
