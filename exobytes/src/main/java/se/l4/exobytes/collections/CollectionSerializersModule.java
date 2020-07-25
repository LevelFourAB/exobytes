package se.l4.exobytes.collections;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import se.l4.exobytes.Serializers;
import se.l4.exobytes.SerializersModule;

public class CollectionSerializersModule
	implements SerializersModule
{
	@Override
	public void activate(Serializers serializers)
	{
		serializers.register(Map.class, new MapSerializerResolver());

		serializers.register(List.class, new MutableCollectionResolver<>(List.class, ArrayList::new));
		serializers.register(ArrayList.class, new MutableCollectionResolver<>(ArrayList.class, ArrayList::new));
		serializers.register(LinkedList.class, new MutableCollectionResolver<>(LinkedList.class, l -> new LinkedList<>()));
		serializers.register(CopyOnWriteArrayList.class, new MutableCollectionResolver<>(CopyOnWriteArrayList.class, l -> new CopyOnWriteArrayList<>()));

		serializers.register(Set.class, new MutableCollectionResolver<>(Set.class, HashSet::new));
		serializers.register(HashSet.class, new MutableCollectionResolver<>(HashSet.class, HashSet::new));
		serializers.register(LinkedHashSet.class, new MutableCollectionResolver<>(LinkedHashSet.class, LinkedHashSet::new));

		serializers.register(NavigableSet.class, new MutableCollectionResolver<>(NavigableSet.class, l -> new TreeSet<>()));
		serializers.register(SortedSet.class, new MutableCollectionResolver<>(SortedSet.class, l -> new TreeSet<>()));
		serializers.register(TreeSet.class, new MutableCollectionResolver<>(TreeSet.class, l -> new TreeSet<>()));

		serializers.register(CopyOnWriteArraySet.class, new MutableCollectionResolver<>(CopyOnWriteArraySet.class, l -> new CopyOnWriteArraySet<>()));
		serializers.register(ConcurrentSkipListSet.class, new MutableCollectionResolver<>(ConcurrentSkipListSet.class, l -> new ConcurrentSkipListSet<>()));

		serializers.register(Queue.class, new MutableCollectionResolver<>(Queue.class, l -> new LinkedList<>()));
	}
}
