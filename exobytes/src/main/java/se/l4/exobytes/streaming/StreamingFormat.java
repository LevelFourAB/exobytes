package se.l4.exobytes.streaming;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import se.l4.exobytes.internal.streaming.LegacyBinaryStreamingFormat;

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
	static JSONStreamingFormat JSON = new JSONStreamingFormat();

	/**
	 * Format for the binary custom format. Available only for legacy and
	 * backwards compatibility reasons. For new code use {@link #CBOR} instead.
	 */
	@Deprecated
	static StreamingFormat LEGACY_BINARY = new LegacyBinaryStreamingFormat();

	/**
	 * Format that reads and writes CBOR.
	 */
	static CBORStreamingFormat CBOR = new CBORStreamingFormat();
}
