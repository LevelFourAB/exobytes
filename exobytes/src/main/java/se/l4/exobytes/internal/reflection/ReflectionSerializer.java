package se.l4.exobytes.internal.reflection;

import java.lang.reflect.Field;
import java.util.Optional;

import org.eclipse.collections.api.factory.Lists;
import org.eclipse.collections.api.factory.Maps;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.ImmutableMap;
import org.eclipse.collections.api.map.MutableMap;

import se.l4.exobytes.AllowAny;
import se.l4.exobytes.Expose;
import se.l4.exobytes.Named;
import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.SkipDefaultValue;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.internal.reflection.properties.SerializableProperty;
import se.l4.ylem.types.reflect.AnnotationLocator;
import se.l4.ylem.types.reflect.ConstructorRef;
import se.l4.ylem.types.reflect.FieldRef;
import se.l4.ylem.types.reflect.TypeRef;

/**
 * Serializer that will use reflection to access fields and methods in a
 * class. Will export anything annotated with {@link Expose}.
 *
 * <p>
 * <ul>
 * 	<li>{@link Named} can be used if you want a field to have a specific name
 * 		in serialized form.
 * 	<li>If you need to use a custom serializer for a field annotate it with
 * 		{@link Use}.
 * 	<li>{@link AllowAny} will cause dynamic serialization to be used for a
 * 		field.
 * </ul>
 */
public class ReflectionSerializer<T>
	implements SerializerResolver<T>
{
	public static final ReflectionSerializer<?> INSTANCE = new ReflectionSerializer<>();

	public ReflectionSerializer()
	{
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Optional<Serializer<T>> find(TypeEncounter encounter)
	{
		TypeRef type = encounter.getType();
		Serializers collection = encounter.getCollection();

		MutableMap<String, SerializableProperty> builder = Maps.mutable.empty();
		MutableMap<String, SerializableProperty> nonRenamedFields = Maps.mutable.empty();

		for(FieldRef field : type.getDeclaredFields())
		{
			if(! field.hasAnnotation(Expose.class))
			{
				continue;
			}

			// Resolve the serializer to use for the field
			Serializer<?> serializer = encounter.get(field.getType());
			boolean skipIfDefault = field.getAnnotation(AnnotationLocator.meta(SkipDefaultValue.class)).isPresent();

			// Force the field to be accessible
			Field reflectiveField = field.getField();
			reflectiveField.setAccessible(true);

			// Define how we access this field
			String name = getName(reflectiveField);
			SerializableProperty property = SerializableProperty.resolveBestForField(name, reflectiveField, serializer, skipIfDefault);
			builder.put(name, property);
			nonRenamedFields.put(reflectiveField.getName(), property);
		}

		// Create field map and cache
		ImmutableMap<String, SerializableProperty> fields = builder.toImmutable();
		ImmutableMap<String, SerializableProperty> nonRenamed = nonRenamedFields.toImmutable();
		SerializableProperty[] fieldsCache = fields.valuesView().toArray(new SerializableProperty[fields.size()]);

		// Get all of the factories
		boolean hasSerializerInFactory = false;
		MutableList<FactoryDefinition<T>> factories = Lists.mutable.empty();

		for(ConstructorRef constructor : type.getConstructors())
		{
			FactoryDefinition<T> def = FactoryDefinition.resolve(collection, type, fields, nonRenamed, constructor);
			if(def == null) continue;

			hasSerializerInFactory |= def.hasSerializedFields();
			factories.add(def);
		}

		if(factories.isEmpty())
		{
			throw new SerializationException("Unable to create any instance of " + type + ", at least a default constructor is needed");
		}

		FactoryDefinition<T>[] factoryCache = factories.toArray(new FactoryDefinition[factories.size()]);

		// Create the actual serializer to use
		TypeInfo<T> typeInfo = new TypeInfo<T>(
			(Class) type.getErasedType(),
			resolveQualifiedName(type),
			factoryCache,
			fields,
			fieldsCache
		);

		if(hasSerializerInFactory)
		{
			FactoryDefinition<T> factoryWithEverything = typeInfo.findSingleFactoryWithEverything();
			if(factoryWithEverything == null)
			{
				// There is no factory that takes in every single field, use a non-streaming serializer
				return Optional.of(new ReflectionNonStreamingSerializer<>(typeInfo));
			}
			else
			{
				return Optional.of(new ReflectionOnlySingleFactorySerializer<>(typeInfo, factoryWithEverything));
			}
		}
		else
		{
			return Optional.of(new ReflectionStreamingSerializer<>(typeInfo));
		}
	}

	private static String getName(Field field)
	{
		if(field.isAnnotationPresent(Expose.class))
		{
			Expose annotation = field.getAnnotation(Expose.class);
			if(! "".equals(annotation.value()))
			{
				return annotation.value();
			}
		}

		return field.getName();
	}

	private static QualifiedName resolveQualifiedName(TypeRef type)
	{
		return type.getAnnotation(Named.class)
			.map(n -> new QualifiedName(n.namespace(), n.name()))
			.orElse(null);
	}
}
