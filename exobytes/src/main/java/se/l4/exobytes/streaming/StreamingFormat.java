package se.l4.exobytes.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.l4.commons.io.IOFunction;
import se.l4.exobytes.internal.cbor.CBORInput;
import se.l4.exobytes.internal.cbor.CBOROutput;
import se.l4.exobytes.internal.streaming.JsonInput;
import se.l4.exobytes.internal.streaming.JsonOutput;
import se.l4.exobytes.internal.streaming.LegacyBinaryInput;
import se.l4.exobytes.internal.streaming.LegacyBinaryOutput;

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
	 * Format for the binary custom format. Available only for legacy and
	 * backwards compatibility reasons. For new code use {@link #CBOR} instead.
	 */
	@Deprecated
	static StreamingFormat LEGACY_BINARY = create(LegacyBinaryInput::new, LegacyBinaryOutput::new);

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
		IOFunction<InputStream, StreamingInput> input,
		IOFunction<OutputStream, StreamingOutput> output
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
