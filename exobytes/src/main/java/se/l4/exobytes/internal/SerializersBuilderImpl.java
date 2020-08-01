package se.l4.exobytes.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import se.l4.exobytes.Serializers;
import se.l4.exobytes.Serializers.Builder;
import se.l4.exobytes.SerializersModule;
import se.l4.exobytes.collections.CollectionSerializersModule;
import se.l4.exobytes.standard.StandardSerializersModule;
import se.l4.ylem.types.instances.DefaultInstanceFactory;
import se.l4.ylem.types.instances.InstanceFactory;

public class SerializersBuilderImpl
	implements Serializers.Builder
{
	private InstanceFactory instanceFactory;
	private boolean includeDefaults;
	private List<SerializersModule> modules;
	private Serializers wrapped;

	public SerializersBuilderImpl()
	{
		instanceFactory = new DefaultInstanceFactory();
		includeDefaults = true;

		modules = new ArrayList<>();
	}

	@Override
	public Builder withInstanceFactory(InstanceFactory factory)
	{
		Objects.requireNonNull(factory, "instance factory can not be null");

		this.instanceFactory = factory;
		return this;
	}

	@Override
	public Builder empty()
	{
		this.includeDefaults = false;
		return this;
	}

	@Override
	public Builder addModule(SerializersModule module)
	{
		Objects.requireNonNull(module, "module can not be null");

		modules.add(module);
		return this;
	}

	@Override
	public Builder wrap(Serializers serializers)
	{
		Objects.requireNonNull(serializers, "can not wrap a null instance");

		this.wrapped = serializers;
		return this;
	}

	@Override
	public Serializers build()
	{
		Serializers instance = wrapped == null
			? new DefaultSerializers(instanceFactory)
			: new WrappedSerializers(instanceFactory, wrapped);

		// First include the defaults if requested
		if(includeDefaults)
		{
			new StandardSerializersModule().activate(instance);
			new CollectionSerializersModule().activate(instance);

			// Attempt to load any modules exported by other projects
			for(SerializersModule module : ServiceLoader.load(SerializersModule.class))
			{
				module.activate(instance);
			}
		}

		// Register all the modules
		for(SerializersModule module : modules)
		{
			module.activate(instance);
		}

		return instance;
	}
}
