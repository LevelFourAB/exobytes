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
public class CompactDynamicSerializer
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
			in.next(Token.LIST_START);

			in.next(in.peek() == Token.NULL ? Token.NULL : Token.VALUE);
			String namespace;
			if(in.current() == Token.NULL)
			{
				namespace = "";
			}
			else
			{
				namespace = in.readString();
			}

			in.next(Token.VALUE);
			String name = in.readString();

			Object result = null;

			Optional<? extends Serializer<?>> serializer = collection.getViaName(namespace, name);
			if(! serializer.isPresent())
			{
				throw new SerializationException("No serializer found for `" + name + (namespace != null ? "` in `" + namespace + "`" : "`"));
			}

			result = in.readObject(serializer.get());

			in.next(Token.LIST_END);
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

			stream.writeListStart();

			if(! qname.getNamespace().equals(""))
			{
				stream.writeString(qname.getNamespace());
			}
			else
			{
				stream.writeNull();
			}

			stream.writeString(qname.getName());

			stream.writeObject((Serializer) serializer, object);

			stream.writeListEnd();
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
