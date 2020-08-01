package se.l4.exobytes.internal;

import java.util.Optional;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.ylem.types.mapping.Resolver;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class SerializerResolverAdapter
	implements Resolver<TypeEncounter, Serializer<?>>
{
	private final SerializerResolver resolver;

	public SerializerResolverAdapter(SerializerResolver resolver)
	{
		this.resolver = resolver;
	}

	@Override
	public Optional<Serializer<?>> resolve(TypeEncounter encounter)
	{
		Optional<SerializerOrResolver<?>> resolved = resolver.find(encounter);
		if(! resolved.isPresent())
		{
			return Optional.empty();
		}

		SerializerOrResolver<?> result = resolved.get();
		if(result instanceof Serializer)
		{
			return (Optional) resolved;
		}

		return ((SerializerResolver) result).find(encounter);
	}
}
