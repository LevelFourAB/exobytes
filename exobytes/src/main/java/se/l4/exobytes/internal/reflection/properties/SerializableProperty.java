package se.l4.exobytes.internal.reflection.properties;

import java.io.IOException;
import java.lang.reflect.Field;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.standard.BooleanSerializer;
import se.l4.exobytes.standard.ByteSerializer;
import se.l4.exobytes.standard.CharacterSerializer;
import se.l4.exobytes.standard.DoubleSerializer;
import se.l4.exobytes.standard.FloatSerializer;
import se.l4.exobytes.standard.IntSerializer;
import se.l4.exobytes.standard.LongSerializer;
import se.l4.exobytes.standard.ShortSerializer;
import se.l4.exobytes.standard.StringSerializer;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;

/**
 * Property that can be serialized, deserialized for use in a factory or
 * deserialized directly into the object.
 */
public abstract class SerializableProperty
{
	protected final String name;
	protected final Class<?> type;
	protected final boolean readOnly;
	protected final boolean skipIfDefault;

	public SerializableProperty(
		String name,
		Class<?> type,
		boolean readOnly,
		boolean skipIfDefault
	)
	{
		this.name = name;
		this.type = type;
		this.readOnly = readOnly;
		this.skipIfDefault = skipIfDefault;
	}

	/**
	 * Get the name that this property is written with.
	 *
	 * @return
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * If this property should be skipped if it's the default value.
	 *
	 * @return
	 */
	public boolean isSkipIfDefault()
	{
		return skipIfDefault;
	}

	/**
	 * If the property is read only.
	 *
	 * @return
	 */
	public boolean isReadOnly()
	{
		return readOnly;
	}

	/**
	 * Get the raw type of the property.
	 *
	 * @return
	 */
	public Class<?> getType()
	{
		return type;
	}

	/**
	 * Read the value of this property and return it as an object.
	 *
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public abstract Object read(StreamingInput in)
		throws IOException;

	/**
	 * Read the value of this property and set it on the given target object.
	 *
	 * @param in
	 * @param obj
	 * @throws IOException
	 */
	public abstract void readAndSet(StreamingInput in, Object obj)
		throws IOException;

	/**
	 * Set a previously {@link #read(StreamingInput) read value} on the given
	 * object.
	 *
	 * @param obj
	 *   the object to set on
	 * @param value
	 *   the value to set
	 * @throws IOException
	 */
	public abstract void set(Object obj, Object value)
		throws IOException;

	/**
	 * Get and write the value to the given output stream, including the name.
	 *
	 * @param obj
	 * @param out
	 * @throws IOException
	 */
	public abstract void write(Object obj, StreamingOutput out)
		throws IOException;

	public static SerializableProperty resolveBestForField(
		String name,
		Field field,
		Serializer<?> serializer,
		boolean skipIfDefault
	)
	{
		Class<?> rawType = field.getType();
		if(serializer instanceof StringSerializer)
		{
			return new StringFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == boolean.class && serializer instanceof BooleanSerializer)
		{
			return new BooleanFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == byte.class && serializer instanceof ByteSerializer)
		{
			return new ByteFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == short.class && serializer instanceof ShortSerializer)
		{
			return new ShortFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == char.class && serializer instanceof CharacterSerializer)
		{
			return new CharFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == int.class && serializer instanceof IntSerializer)
		{
			return new IntFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == long.class && serializer instanceof LongSerializer)
		{
			return new LongFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == float.class && serializer instanceof FloatSerializer)
		{
			return new FloatFieldProperty(name, field, skipIfDefault);
		}
		else if(rawType == double.class && serializer instanceof DoubleSerializer)
		{
			return new DoubleFieldProperty(name, field, skipIfDefault);
		}

		return new ObjectFieldProperty(name, field, serializer, skipIfDefault);
	}
}
