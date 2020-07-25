package se.l4.exobytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Before;

import se.l4.commons.io.IOConsumer;
import se.l4.commons.io.IOSupplier;
import se.l4.exobytes.format.StreamingFormat;
import se.l4.exobytes.format.StreamingInput;
import se.l4.exobytes.format.StreamingOutput;

/**
 * Abstract base class that provides an instance of {@link Serializers}.
 */
public abstract class SerializerTest
{
	protected Serializers serializers;

	@Before
	public void provideSerializers()
	{
		serializers = Serializers.create()
			.build();
	}

	protected IOSupplier<StreamingInput> write(IOConsumer<StreamingOutput> output)
		throws IOException
	{
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try(StreamingOutput out = StreamingFormat.CBOR.createOutput(stream))
		{
			output.accept(out);
		}

		byte[] input = stream.toByteArray();
		return () -> StreamingFormat.CBOR.createInput(new ByteArrayInputStream(input));
	}
}
