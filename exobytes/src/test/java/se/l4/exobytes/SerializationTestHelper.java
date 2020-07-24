package se.l4.exobytes;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Function;

import se.l4.exobytes.format.LegacyBinaryInput;
import se.l4.exobytes.format.LegacyBinaryOutput;
import se.l4.exobytes.format.JsonInput;
import se.l4.exobytes.format.JsonOutput;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;
import se.l4.exobytes.internal.format.CBORInput;
import se.l4.exobytes.internal.format.CBOROutput;

public class SerializationTestHelper
{
	private SerializationTestHelper()
	{
	}

	public static <T> void testWriteAndRead(Serializer<T> serializer, T object)
	{
		testWriteAndRead(serializer, object, LegacyBinaryInput::new, LegacyBinaryOutput::new);
		testWriteAndRead(serializer, object, JsonInput::new, JsonOutput::new);
		testWriteAndRead(serializer, object, CBORInput::new, CBOROutput::new);
	}

	public static <T> void testWriteAndRead(
		Serializer<T> serializer,
		T object,
		Function<InputStream, StreamingInput> inputFactory,
		Function<OutputStream, StreamingOutput> outputFactory
	)
	{
		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			try(StreamingOutput so = outputFactory.apply(out))
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
			try(StreamingInput si = inputFactory.apply(in))
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
