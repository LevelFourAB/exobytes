package se.l4.exobytes;

import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Implementation of {@link Serializers} that wraps another
 * collection.
 */
public class WrappedSerializers
	extends AbstractSerializers
{
	private final Serializers other;

	public WrappedSerializers(Serializers other)
	{
		this.other = other;
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return other.getInstanceFactory();
	}

	@Override
	public Serializer<?> find(TypeRef type)
	{
		try
		{
			return super.find(type);
		}
		catch(SerializationException e)
		{
			return other.find(type);
		}
	}
}
