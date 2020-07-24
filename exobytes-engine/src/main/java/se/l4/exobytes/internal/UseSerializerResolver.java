package se.l4.exobytes.internal;

import java.util.Optional;

import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.Use;

public class UseSerializerResolver
	implements SerializerResolver<Object>
{
	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<? extends SerializerOrResolver<Object>> find(TypeEncounter encounter)
	{
		// A specific serializer should be used
		Optional<Use> annotation = encounter.getHint(Use.class);
		if(! annotation.isPresent())
		{
			return Optional.empty();
		}

		Class<? extends SerializerOrResolver> value = annotation.get().value();
		return (Optional) Optional.of(
			encounter.find((Class) value, encounter.getType())
		);
	}
}
