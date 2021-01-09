package se.l4.exobytes.time;

import java.io.IOException;
import java.time.Period;
import java.util.Optional;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * Serializers for {@link Period}.
 */
public class PeriodSerializers
	implements SerializerResolver<Period>
{
	@Override
	public Optional<? extends SerializerOrResolver<Period>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(Period.class))
		{
			return Optional.empty();
		}

		if(encounter.getHint(TemporalHints.Timestamp.class).isPresent())
		{
			throw new SerializationException("TemporalHints.Timestamp is not supported");
		}

		if(encounter.getHint(TemporalHints.CustomFormat.class).isPresent())
		{
			throw new SerializationException("TemporalHints.CustomFormat is not supported");
		}

		Optional<TemporalHints.Format> format = encounter.getHint(TemporalHints.Format.class);
		if(format.isPresent())
		{
			if(format.get().value() != TemporalHints.StandardFormat.DEFAULT)
			{
				throw new SerializationException("Only TemporalHints.StandardFormat.DEFAULT is supported");
			}

			return Optional.of(new PeriodStringSerializer());
		}

		return Optional.empty();
	}

	private static class PeriodStringSerializer
		implements Serializer<Period>
	{
		@Override
		public Period read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return Period.parse(in.readString());
		}

		@Override
		public void write(Period object, StreamingOutput out)
			throws IOException
		{
			out.writeString(object.toString());
		}

		@Override
		public int hashCode()
		{
			return getClass().hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof PeriodStringSerializer;
		}

		@Override
		public String toString()
		{
			return "PeriodStringSerializer{}";
		}
	}
}
