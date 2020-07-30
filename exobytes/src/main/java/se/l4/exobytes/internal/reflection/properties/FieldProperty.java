package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import se.l4.commons.types.Types;
import se.l4.exobytes.SerializationException;

/**
 * {@link SerializableProperty} that works on a {@link Field} within a class.
 */
public abstract class FieldProperty
	extends SerializableProperty
{
	protected final Field field;

	public FieldProperty(
		String name,
		Field field,
		boolean skipIfDefault
	)
	{
		super(name, field.getType(), Modifier.isFinal(field.getModifiers()), skipIfDefault);

		this.field = field;
	}

	@Override
	public void set(Object obj, Object value)
		throws IOException
	{
		try
		{
			if(value == null && type.isPrimitive())
			{
				value = Types.defaultValue(type);
			}

			field.set(obj, value);
		}
		catch(Exception e)
		{
			throw new SerializationException("Unable to read object; " + e.getMessage(), e);
		}
	}

	protected final Object get(Object obj)
	{
		try
		{
			return field.get(obj);
		}
		catch(IllegalArgumentException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}
		catch(IllegalAccessException e)
		{
			throw new SerializationException("Unable to write object; " + e.getMessage(), e);
		}
	}
}
