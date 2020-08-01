package se.l4.exobytes.internal;

import se.l4.exobytes.Serializers;
import se.l4.ylem.types.instances.DefaultInstanceFactory;
import se.l4.ylem.types.instances.InstanceFactory;

/**
 * Default implementation of {@link Serializers}.
 */
public class DefaultSerializers
	extends AbstractSerializers
{
	private final InstanceFactory instanceFactory;

	public DefaultSerializers()
	{
		this(new DefaultInstanceFactory());
	}

	public DefaultSerializers(InstanceFactory instanceFactory)
	{
		this.instanceFactory = instanceFactory;
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}
}
