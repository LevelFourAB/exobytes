package se.l4.exobytes;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;

public class DefaultSerializersTest
{
	private Serializers serializers;

	@BeforeEach
	public void before()
	{
		serializers = Serializers.create()
			.build();
	}

	@Test
	public void testUseAnnotation()
	{
		serializers.get(ClassWithUse.class);
	}

	@Test
	public void testUseAnnotationExtension()
	{
		try
		{
			serializers.get(ClassExtendingUse.class);
		}
		catch(SerializationException e)
		{
			return;
		}

		throw new AssertionError("Should not be able to resolve sub-class serializer without @Use");
	}

	@Test
	public void testString()
	{
		Serializer<String> string = serializers.get(String.class);
	}

	@Use(FakeSerializer.class)
	public static class ClassWithUse
	{
	}

	public static class ClassExtendingUse
		extends ClassWithUse
	{

	}

	public static class FakeSerializer
		implements Serializer<ClassWithUse>
	{
		@Override
		public ClassWithUse read(StreamingInput in)
			throws IOException
		{
			return null;
		}

		@Override
		public void write(ClassWithUse object, StreamingOutput out)
			throws IOException
		{
		}
	}
}
