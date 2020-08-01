package se.l4.exobytes.internal.cbor;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * A @{link OutputStream} that will send chunks of the written data to the
 * given @{link Control}.
 */
public class CBORChunkOutputStream
	extends OutputStream
{
	private final Control out;
	private final byte[] buffer;
	private int len;

	public CBORChunkOutputStream(int size, @NonNull Control out)
	{
		this.out = Objects.requireNonNull(out);
		buffer = new byte[size];
	}

	@Override
	public void write(int b)
		throws IOException
	{
		buffer[len++] = (byte) b;
		if(len == buffer.length)
		{
			out.consume(buffer, 0, len);
			len = 0;
		}
	}

	@Override
	public void flush()
		throws IOException
	{
		if(len != 0)
		{
			out.consume(buffer, 0, len);
			len = 0;
		}
	}

	@Override
	public void close()
		throws IOException
	{
		if(len != 0)
		{
			out.consume(buffer, 0, len);
			len = 0;
		}
	}

	interface Control
	{
		void consume(@NonNull byte[] data, int offset, int length)
			throws IOException;
	}
}
