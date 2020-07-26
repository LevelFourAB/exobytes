package se.l4.exobytes.streaming;

import java.io.IOException;

import org.junit.jupiter.api.Test;

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
		return StreamingFormat.LEGACY_BINARY;
	}

	@Override
	@Test
	public void testConversionByteToChar() throws IOException {
		// TODO Auto-generated method stub
		super.testConversionByteToChar();
	}
}
