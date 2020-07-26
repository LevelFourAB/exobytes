package se.l4.exobytes;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

	@Use(ReflectionSerializer.class)
	public static class ClassWithUse
	{
	}

	public static class ClassExtendingUse
		extends ClassWithUse
	{

	}
}
