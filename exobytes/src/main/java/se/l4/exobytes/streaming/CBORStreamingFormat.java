package se.l4.exobytes.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.l4.exobytes.internal.cbor.CBORInput;
import se.l4.exobytes.internal.cbor.CBOROutput;

/**
 * {@link StreamingFormat} for CBOR.
 */
public class CBORStreamingFormat
	implements StreamingFormat
{
	@Override
	public StreamingInput createInput(InputStream in)
		throws IOException
	{
		return new CBORInput(in);
	}

	@Override
	public StreamingOutput createOutput(OutputStream out)
		throws IOException
	{
		return new CBOROutput(out);
	}
}
