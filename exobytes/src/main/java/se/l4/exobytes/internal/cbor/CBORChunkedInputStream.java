package se.l4.exobytes.internal.cbor;

import java.io.IOException;
import java.io.InputStream;

/**
 * Stream used to implement support for CBOR streaming.
 */
public class CBORChunkedInputStream
	extends InputStream
{
	private final Control control;
	private int bytesRemaining;

	public CBORChunkedInputStream(Control control)
		throws IOException
	{
		this.control = control;

		bytesRemaining = control.readChunkLength();
	}

	@Override
	public int read()
		throws IOException
	{
		if(bytesRemaining == 0)
		{
			bytesRemaining = control.readChunkLength();
		}
		else if(bytesRemaining == -1)
		{
			return -1;
		}

		bytesRemaining--;
		return control.read();
	}

	@Override
	public int read(byte[] b, int off, int len)
		throws IOException
	{
		if(bytesRemaining == 0)
		{
			bytesRemaining = control.readChunkLength();
		}
		else if(bytesRemaining == -1)
		{
			return -1;
		}

		if(len > bytesRemaining)
		{
			// If more bytes are requested than available limit how much is read
			len = bytesRemaining;
		}

		int readBytes = control.read(b, off, len);
		bytesRemaining -= readBytes;
		return readBytes;
	}

	@Override
	public void close()
		throws IOException
	{
		while(bytesRemaining >= 0)
		{
			if(bytesRemaining == 0)
			{
				bytesRemaining = control.readChunkLength();
			}

			if(bytesRemaining > 0)
			{
				control.skip(bytesRemaining);
				bytesRemaining = 0;
			}
		}

		control.close();
	}

	interface Control
	{
		/**
		 * Read the next chunk length.
		 *
		 * @return
		 */
		int readChunkLength()
			throws IOException;

		/**
		 * Read a single byte.
		 */
		int read()
			throws IOException;

		/**
		 * Read a number of bytes into the given buffer.
		 */
		int read(byte[] buf, int offset, int length)
			throws IOException;

		void skip(int bytes)
			throws IOException;

		/**
		 * Indicate that the stream is being closed.
		 */
		void close()
			throws IOException;
	}
}
