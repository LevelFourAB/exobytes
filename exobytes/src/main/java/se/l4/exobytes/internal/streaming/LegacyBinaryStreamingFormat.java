package se.l4.exobytes.internal.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.l4.exobytes.streaming.StreamingFormat;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;

/**
 * Format that creates inputs and outputs for the legacy binary format.
 */
@SuppressWarnings("deprecation")
public class LegacyBinaryStreamingFormat
	implements StreamingFormat
{
	@Override
	public StreamingInput createInput(InputStream in)
		throws IOException
	{
		return new LegacyBinaryInput(in);
	}

	@Override
	public StreamingOutput createOutput(OutputStream out)
		throws IOException
	{
		return new LegacyBinaryOutput(out);
	}
}
