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
		serializers.bind(Map.class, new MapSerializerResolver());

		serializers.bind(List.class, new MutableCollectionResolver<>(List.class, ArrayList::new));
		serializers.bind(ArrayList.class, new MutableCollectionResolver<>(ArrayList.class, ArrayList::new));
		serializers.bind(LinkedList.class, new MutableCollectionResolver<>(LinkedList.class, l -> new LinkedList<>()));
		serializers.bind(CopyOnWriteArrayList.class, new MutableCollectionResolver<>(CopyOnWriteArrayList.class, l -> new CopyOnWriteArrayList<>()));

		serializers.bind(Set.class, new MutableCollectionResolver<>(Set.class, HashSet::new));
		serializers.bind(HashSet.class, new MutableCollectionResolver<>(HashSet.class, HashSet::new));
		serializers.bind(LinkedHashSet.class, new MutableCollectionResolver<>(LinkedHashSet.class, LinkedHashSet::new));

		serializers.bind(NavigableSet.class, new MutableCollectionResolver<>(NavigableSet.class, l -> new TreeSet<>()));
		serializers.bind(SortedSet.class, new MutableCollectionResolver<>(SortedSet.class, l -> new TreeSet<>()));
		serializers.bind(TreeSet.class, new MutableCollectionResolver<>(TreeSet.class, l -> new TreeSet<>()));

		serializers.bind(CopyOnWriteArraySet.class, new MutableCollectionResolver<>(CopyOnWriteArraySet.class, l -> new CopyOnWriteArraySet<>()));
		serializers.bind(ConcurrentSkipListSet.class, new MutableCollectionResolver<>(ConcurrentSkipListSet.class, l -> new ConcurrentSkipListSet<>()));

		serializers.bind(Queue.class, new MutableCollectionResolver<>(Queue.class, l -> new LinkedList<>()));
	}
}
