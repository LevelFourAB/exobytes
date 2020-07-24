package se.l4.exobytes.collections;

import java.util.List;
import java.util.Optional;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

public class ListSerializerResolver
	implements SerializerResolver<List<?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<List<?>>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(List.class))
		{
			return Optional.empty();
		}

		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.of(new ListSerializer(encounter.find(type)));
	}
}
