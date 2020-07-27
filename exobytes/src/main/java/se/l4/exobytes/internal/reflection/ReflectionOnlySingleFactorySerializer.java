package se.l4.exobytes.internal.reflection;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

import se.l4.commons.types.Types;
import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.internal.reflection.properties.SerializableProperty;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer that uses a smarter mapping creating instances using a single factory.
 *
 * @param <T>
 */
public class ReflectionOnlySingleFactorySerializer<T>
	implements Serializer<T>
{
	private final TypeInfo<T> type;
	private final FactoryDefinition<T> factory;
	private final Object[] defaultArguments;

	private final String[] names;
	private final SerializableProperty[] fields;
	private final int[] mapping;

	public ReflectionOnlySingleFactorySerializer(TypeInfo<T> type, FactoryDefinition<T> factory)
	{
		this.type = type;
		this.factory = factory;

		Map<String, Integer> tempMapping = new TreeMap<>();
		Object[] defaultArguments = new Object[factory.arguments.length];
		for(int i=0, n=factory.arguments.length; i<n; i++)
		{
			FactoryDefinition.Argument arg = factory.arguments[i];
			if(arg instanceof FactoryDefinition.SerializedArgument)
			{
				FactoryDefinition.SerializedArgument serializedArg = (FactoryDefinition.SerializedArgument) arg;

				String name = serializedArg.name;
				tempMapping.put(name, i);
				defaultArguments[i] = Types.defaultValue(serializedArg.type);

			}
		}
		this.defaultArguments = defaultArguments;

		String[] names = new String[tempMapping.size()];
		SerializableProperty[] fields = new SerializableProperty[tempMapping.size()];
		int[] mapping = new int[tempMapping.size()];
		int i = 0;
		for(Map.Entry<String, Integer> e : tempMapping.entrySet())
		{
			names[i] = e.getKey();
			fields[i] = type.getProperty(e.getKey());
			mapping[i] = e.getValue();

			i++;

		}

		this.names = names;
		this.fields = fields;
		this.mapping = mapping;
	}

	@Override
	public Optional<QualifiedName> getName()
	{
		return Optional.ofNullable(type.getName());
	}

	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		in.next(Token.OBJECT_START);

		Object[] args = Arrays.copyOf(defaultArguments, defaultArguments.length);

		while(in.peek() != Token.OBJECT_END)
		{
			in.next(Token.KEY);
			String key = in.readString();

			int idx = Arrays.binarySearch(names, key);
			if(idx >= 0)
			{
				args[mapping[idx]] = fields[idx].read(in);
			}
			else
			{
				in.skipNext();
			}
		}

		in.next(Token.OBJECT_END);
		return factory.create(args);
	}

	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		stream.writeObjectStart();

		for(SerializableProperty def : type.getProperties())
		{
			def.write(object, stream);
		}

		stream.writeObjectEnd();
	}
}
