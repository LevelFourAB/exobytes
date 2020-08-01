package se.l4.exobytes.internal;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.Serializers;
import se.l4.ylem.types.instances.InstanceFactory;
import se.l4.ylem.types.reflect.TypeRef;

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
	public Serializer<?> get(TypeRef type)
	{
		try
		{
			return super.get(type);
		}
		catch(SerializationException e)
		{
			return other.get(type);
		}
	}
}
