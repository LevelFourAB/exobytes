package se.l4.exobytes.standard;

import java.util.Optional;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.commons.types.Types;
import se.l4.commons.types.reflect.TypeRef;

/**
 * Resolver that resolves a suitable {@link OptionalSerializer} based on
 * the type declared.
 */
public class OptionalSerializerResolver
	implements SerializerResolver<Optional<?>>
{
	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Optional<Serializer<Optional<?>>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType()
			.getTypeParameter(0)
			.orElseGet(() -> Types.reference(Object.class));

		return Optional.ofNullable(new OptionalSerializer(encounter.find(type)));
	}
}
