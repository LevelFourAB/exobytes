package se.l4.exobytes.format;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import se.l4.exobytes.internal.format.CBORInput;
import se.l4.exobytes.internal.format.CBOROutput;

/**
 * Format used to read or write objects via a {@link se.l4.exobytes.Serializer}.
 */
public interface StreamingFormat
{
	/**
	 * Create a {@link StreamingInput} for the given stream.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	StreamingInput createInput(InputStream in)
		throws IOException;

	/**
	 * Create a {@link StreamingOutput} for the given stream.
	 *
	 * @param out
	 * @return
	 * @throws IOException
	 */
	StreamingOutput createOutput(OutputStream out)
		throws IOException;


	/**
	 * Format for JSON.
	 */
	static StreamingFormat JSON = create(JsonInput::new, JsonOutput::new);

	/**
	 * Format for the binary custom format.
	 */
	static StreamingFormat BINARY = create(BinaryInput::new, BinaryOutput::new);

	/**
	 * Format that reads and writes CBOR.
	 */
	static StreamingFormat CBOR = create(CBORInput::new, CBOROutput::new);

	/**
	 * Create an instance of {@link StreamingFormat}.
	 *
	 * @param input
	 * @param output
	 * @return
	 */
	static StreamingFormat create(
		Function<InputStream, StreamingInput> input,
		Function<OutputStream, StreamingOutput> output
	)
	{
		return new StreamingFormat()
		{
			@Override
			public StreamingInput createInput(InputStream in)
				throws IOException
			{
				return input.apply(in);
			}

			@Override
			public StreamingOutput createOutput(OutputStream out)
				throws IOException
			{
				return output.apply(out);
			}
		};
	}
}
