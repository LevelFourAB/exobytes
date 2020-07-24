package se.l4.exobytes.standard;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import org.junit.Test;

import se.l4.exobytes.format.LegacyBinaryInput;
import se.l4.exobytes.format.LegacyBinaryOutput;

public class UuidSerializerTest
{
	@Test
	public void testOne()
	{
		UUID uuid = UUID.fromString("29fa14b7-6fb6-4d68-bec1-40307a949421");
		UUID second = writeAndRead(uuid);

		assertThat(second, is(uuid));
	}

	private UUID writeAndRead(UUID uuid)
	{
		UuidSerializer serializer = new UuidSerializer();

		try
		{
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			serializer.write(uuid, new LegacyBinaryOutput(out));

			return serializer.read(new LegacyBinaryInput(new ByteArrayInputStream(out.toByteArray())));
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}
