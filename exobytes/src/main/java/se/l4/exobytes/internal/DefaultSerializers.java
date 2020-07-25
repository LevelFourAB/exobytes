package se.l4.exobytes.internal;

import se.l4.commons.types.DefaultInstanceFactory;
import se.l4.commons.types.InstanceFactory;
import se.l4.exobytes.Serializers;

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
