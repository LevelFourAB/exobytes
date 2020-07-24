package se.l4.exobytes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import se.l4.commons.io.Bytes;
import se.l4.commons.types.DefaultInstanceFactory;
import se.l4.commons.types.InstanceFactory;
import se.l4.exobytes.collections.MapSerializerResolver;
import se.l4.exobytes.collections.MutableCollectionResolver;
import se.l4.exobytes.standard.BooleanSerializer;
import se.l4.exobytes.standard.ByteArraySerializer;
import se.l4.exobytes.standard.ByteSerializer;
import se.l4.exobytes.standard.BytesSerializer;
import se.l4.exobytes.standard.CharacterSerializer;
import se.l4.exobytes.standard.DoubleSerializer;
import se.l4.exobytes.standard.FloatSerializer;
import se.l4.exobytes.standard.IntSerializer;
import se.l4.exobytes.standard.LongSerializer;
import se.l4.exobytes.standard.OptionalSerializer;
import se.l4.exobytes.standard.ShortSerializer;
import se.l4.exobytes.standard.StringSerializer;
import se.l4.exobytes.standard.UuidSerializer;

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

		// Standard types
		bind(Boolean.class, new BooleanSerializer());
		bind(Byte.class, new ByteSerializer());
		bind(Character.class, new CharacterSerializer());
		bind(Double.class, new DoubleSerializer());
		bind(Float.class, new FloatSerializer());
		bind(Integer.class, new IntSerializer());
		bind(Long.class, new LongSerializer());
		bind(Short.class, new ShortSerializer());
		bind(String.class, new StringSerializer());
		bind(byte[].class, new ByteArraySerializer());
		bind(UUID.class, new UuidSerializer());

		// Collections
		bind(Map.class, new MapSerializerResolver());

		bind(List.class, new MutableCollectionResolver<>(List.class, ArrayList::new));
		bind(ArrayList.class, new MutableCollectionResolver<>(ArrayList.class, ArrayList::new));
		bind(LinkedList.class, new MutableCollectionResolver<>(LinkedList.class, l -> new LinkedList<>()));
		bind(CopyOnWriteArrayList.class, new MutableCollectionResolver<>(CopyOnWriteArrayList.class, l -> new CopyOnWriteArrayList<>()));

		bind(Set.class, new MutableCollectionResolver<>(Set.class, HashSet::new));
		bind(HashSet.class, new MutableCollectionResolver<>(HashSet.class, HashSet::new));
		bind(LinkedHashSet.class, new MutableCollectionResolver<>(LinkedHashSet.class, LinkedHashSet::new));

		bind(NavigableSet.class, new MutableCollectionResolver<>(NavigableSet.class, l -> new TreeSet<>()));
		bind(SortedSet.class, new MutableCollectionResolver<>(SortedSet.class, l -> new TreeSet<>()));
		bind(TreeSet.class, new MutableCollectionResolver<>(TreeSet.class, l -> new TreeSet<>()));

		bind(CopyOnWriteArraySet.class, new MutableCollectionResolver<>(CopyOnWriteArraySet.class, l -> new CopyOnWriteArraySet<>()));
		bind(ConcurrentSkipListSet.class, new MutableCollectionResolver<>(ConcurrentSkipListSet.class, l -> new ConcurrentSkipListSet<>()));

		bind(Queue.class, new MutableCollectionResolver<>(Queue.class, l -> new LinkedList<>()));

		// Optional<T>
		bind(Optional.class, new OptionalSerializer());

		bind(Bytes.class, new BytesSerializer());
	}

	@Override
	public InstanceFactory getInstanceFactory()
	{
		return instanceFactory;
	}
}
