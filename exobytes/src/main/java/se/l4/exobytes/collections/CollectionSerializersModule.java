package se.l4.exobytes.collections;

import java.util.ArrayList;
import java.util.HashMap;
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
		serializers.register(Map.class, new MapResolver<>(Map.class, HashMap::new));
		serializers.register(HashMap.class, new MapResolver<>(HashMap.class, HashMap::new));

		serializers.register(List.class, new CollectionResolver<>(List.class, ArrayList::new));
		serializers.register(ArrayList.class, new CollectionResolver<>(ArrayList.class, ArrayList::new));
		serializers.register(LinkedList.class, new CollectionResolver<>(LinkedList.class, l -> new LinkedList<>()));
		serializers.register(CopyOnWriteArrayList.class, new CollectionResolver<>(CopyOnWriteArrayList.class, l -> new CopyOnWriteArrayList<>()));

		serializers.register(Set.class, new CollectionResolver<>(Set.class, HashSet::new));
		serializers.register(HashSet.class, new CollectionResolver<>(HashSet.class, HashSet::new));
		serializers.register(LinkedHashSet.class, new CollectionResolver<>(LinkedHashSet.class, LinkedHashSet::new));

		serializers.register(NavigableSet.class, new CollectionResolver<>(NavigableSet.class, l -> new TreeSet<>()));
		serializers.register(SortedSet.class, new CollectionResolver<>(SortedSet.class, l -> new TreeSet<>()));
		serializers.register(TreeSet.class, new CollectionResolver<>(TreeSet.class, l -> new TreeSet<>()));

		serializers.register(CopyOnWriteArraySet.class, new CollectionResolver<>(CopyOnWriteArraySet.class, l -> new CopyOnWriteArraySet<>()));
		serializers.register(ConcurrentSkipListSet.class, new CollectionResolver<>(ConcurrentSkipListSet.class, l -> new ConcurrentSkipListSet<>()));

		serializers.register(Queue.class, new CollectionResolver<>(Queue.class, l -> new LinkedList<>()));
	}
}
