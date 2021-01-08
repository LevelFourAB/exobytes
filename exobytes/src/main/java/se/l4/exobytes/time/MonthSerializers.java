package se.l4.exobytes.time;

import java.io.IOException;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

/**
 * {@link SerializerResolver} for {@link Month}.
 */
public class MonthSerializers
	implements SerializerResolver<Month>
{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM");

	@Override
	public Optional<? extends SerializerOrResolver<Month>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(Month.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<Month>> result =
			TemporalSerializers.findFormattingSerializer(
				encounter,
				Month::from,
				FORMATTER,
				Sets.immutable.of(Temporal.StandardFormat.DEFAULT)
			);

		if(result.isPresent())
		{
			return result;
		}

		if(encounter.getHint(Temporal.Timestamp.class).isPresent())
		{
			throw new SerializationException("Temporal.Timestamp is not supported");
		}

		return Optional.of(new MonthIntSerializer());
	}

	private static class MonthIntSerializer
		implements Serializer<Month>
	{
		@Override
		public Month read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return Month.of(in.readInt());
		}

		@Override
		public void write(Month object, StreamingOutput out)
			throws IOException
		{
			out.writeInt(object.getValue());
		}

		@Override
		public int hashCode()
		{
			return getClass().hashCode();
		}

		@Override
		public boolean equals(Object obj)
		{
			return obj instanceof MonthIntSerializer;
		}

		@Override
		public String toString()
		{
			return "MonthIntSerializer{}";
		}
	}
}
