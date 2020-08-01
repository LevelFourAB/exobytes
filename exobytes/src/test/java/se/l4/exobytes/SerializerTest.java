package se.l4.exobytes;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;

import se.l4.exobytes.streaming.StreamingFormat;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.ylem.io.IOConsumer;
import se.l4.ylem.io.IOSupplier;

/**
 * Abstract base class that provides an instance of {@link Serializers}.
 */
public abstract class SerializerTest
{
	protected Serializers serializers;

	@BeforeEach
	public void provideSerializers()
	{
		serializers = Serializers.create()
			.build();
	}

	protected Serializers emptySerializers()
	{
		return Serializers.create()
			.empty()
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
