package se.l4.exobytes.standard;

import java.io.IOException;
import java.util.Optional;

import se.l4.exobytes.QualifiedName;
import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.Serializers;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializer that will attempt to dynamically resolve serializers based on
 * their name.
 */
public class DynamicSerializer
	implements SerializerResolver<Object>
{
	@Override
	public Optional<? extends SerializerOrResolver<Object>> find(TypeEncounter encounter)
	{
		return Optional.of(new Impl(encounter.getCollection()));
	}

	public static final class Impl
		implements Serializer<Object>
	{
		private final Serializers collection;

		public Impl(Serializers collection)
		{
			this.collection = collection;
		}

		@Override
		public Object read(StreamingInput in)
			throws IOException
		{
			// Read start of object
			in.next(Token.OBJECT_START);

			String namespace = "";
			String name = null;

			Object result = null;
			boolean resultRead = false;

			/*
			* Loop through values, first reading namespace and name. If value
			* is encountered before name abort.
			*/
			while(in.peek() != Token.OBJECT_END)
			{
				in.next(Token.VALUE);
				String key = in.readString();

				if("namespace".equals(key))
				{
					in.next(Token.VALUE);

					String value = in.readString();
					namespace = value;
				}
				else if("name".equals(key))
				{
					in.next(Token.VALUE);
					String value = in.readString();

					name = value;
				}
				else if("value".equals(key))
				{
					if(name == null)
					{
						throw new SerializationException("Name of type must come before dynamic value");
					}

					resultRead = true;

					Optional<? extends Serializer<?>> serializer = collection.getViaName(namespace, name);
					if(! serializer.isPresent())
					{
						throw new SerializationException("No serializer found for `" + name + ("".equals(namespace) ? "`" : "` in `" + namespace + "`"));
					}

					result = in.readObject(serializer.get());
				}
				else
				{
					in.skipNext();
				}
			}

			if(! resultRead)
			{
				throw new SerializationException("Dynamic serialization requires a value");
			}

			in.next(Token.OBJECT_END);
			return result;
		}

		@Override
		@SuppressWarnings({ "rawtypes", "unchecked" })
		public void write(Object object, StreamingOutput stream)
			throws IOException
		{
			Serializer<?> serializer = collection.get(object.getClass());

			QualifiedName qname = serializer.getName()
				.orElseThrow(() -> new SerializationException("Tried to use dynamic serialization for " + object.getClass() + ", but type has no name"));

			stream.writeObjectStart();

			if(! qname.getNamespace().equals(""))
			{
				stream.writeString("namespace");
				stream.writeString(qname.getNamespace());
			}

			stream.writeString("name");
			stream.writeString(qname.getName());

			stream.writeString("value");
			stream.writeObject((Serializer) serializer, object);

			stream.writeObjectEnd();
		}

		@Override
		public int hashCode()
		{
			return getClass().hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj != null && (this == obj || getClass() == obj.getClass());
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{}";
		}
	}
}
