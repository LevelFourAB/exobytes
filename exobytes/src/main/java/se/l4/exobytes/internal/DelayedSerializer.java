package se.l4.exobytes.internal;

import java.io.IOException;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.ylem.types.reflect.TypeRef;

/**
 * Serializer that is delayed in that it will not be assigned until the entire
 * serializer chain is resolved. Used to solve recursive serialization.
 *
 * @param <T>
 */
public class DelayedSerializer<T>
	implements Serializer.NullHandling<T>
{
	private volatile Serializer<T> instance;

	public DelayedSerializer(Serializers collection, TypeRef type)
	{
		instance = new Serializer<T>()
		{
			@SuppressWarnings("unchecked")
			private void ensureSerializer()
			{
				Serializer<T> resolved = (Serializer<T>) collection.get(type);
				if(resolved instanceof DelayedSerializer)
				{
					return;
				}

				instance = resolved;
			}

			@Override
			public T read(StreamingInput in)
				throws IOException
			{
				ensureSerializer();

				return in.readObject(instance);
			}


			@Override
			public void write(T object, StreamingOutput stream)
				throws IOException
			{
				ensureSerializer();

				stream.writeObject(instance, object);
			}
		};
	}

	@Override
	public T read(StreamingInput in)
		throws IOException
	{
		return in.readObject(instance);
	}

	@Override
	public void write(T object, StreamingOutput stream)
		throws IOException
	{
		stream.writeObject(instance, object);
	}
}
