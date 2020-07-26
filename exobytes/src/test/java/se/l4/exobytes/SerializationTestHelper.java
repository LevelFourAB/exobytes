package se.l4.exobytes;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import se.l4.exobytes.streaming.StreamingFormat;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;

public class SerializationTestHelper
{
	private SerializationTestHelper()
	{
	}

	public static <T> void testWriteAndRead(Serializer<T> serializer, T object)
	{
		testWriteAndRead(serializer, object, StreamingFormat.LEGACY_BINARY);
		testWriteAndRead(serializer, object, StreamingFormat.JSON);
		testWriteAndRead(serializer, object, StreamingFormat.CBOR);
	}

	public static <T> void testWriteAndRead(
		Serializer<T> serializer,
		T object,
		StreamingFormat format
	)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try(StreamingOutput so = format.createOutput(out))
			{
				try
				{
					serializer.write(object, so);
				}
				catch(IOException e)
				{
					throw new RuntimeException("Could not write via " + so.getClass().getName(), e);
				}
			}

			ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
			try(StreamingInput si = format.createInput(in))
			{
				try
				{
					T value = serializer.read(si);

					assertThat(value, is(object));
				}
				catch(IOException e)
				{
					throw new RuntimeException("Could not read via " + si.getClass().getName(), e);
				}
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
