package se.l4.exobytes.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.l4.exobytes.internal.streaming.JsonInput;
import se.l4.exobytes.internal.streaming.JsonOutput;

/**
 * {@link StreamingFormat} for JSON.
 */
public class JSONStreamingFormat
	implements StreamingFormat
{
	@Override
	public StreamingInput createInput(InputStream in)
		throws IOException
	{
		return new JsonInput(in);
	}

	@Override
	public StreamingOutput createOutput(OutputStream out)
		throws IOException
	{
		return new JsonOutput(out);
	}
}
