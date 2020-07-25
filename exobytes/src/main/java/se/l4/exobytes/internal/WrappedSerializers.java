package se.l4.exobytes.internal;

import se.l4.commons.types.InstanceFactory;
import se.l4.commons.types.reflect.TypeRef;
import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.Serializers;

/**
 * Implementation of {@link Serializers} that wraps another
 * collection.
 */
public class WrappedSerializers
	extends AbstractSerializers
{
	private final InstanceFactory instanceFactory;
	private final Serializers other;

	public WrappedSerializers(InstanceFactory instanceFactory, Serializers other)
	{
		this.other = other;
		this.instanceFactory = instanceFactory;
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
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
