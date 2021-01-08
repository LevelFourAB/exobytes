package se.l4.exobytes.time;

import java.io.IOException;
import java.time.DayOfWeek;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Locale;
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

public class DayOfWeekSerializers
	implements SerializerResolver<DayOfWeek>
{
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.DAY_OF_WEEK)
		.toFormatter(Locale.ENGLISH);

	@Override
	public Optional<? extends SerializerOrResolver<DayOfWeek>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(DayOfWeek.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<DayOfWeek>> result =
			TemporalSerializers.findFormattingSerializer(
				encounter,
				DayOfWeek::from,
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

		return Optional.of(new DayOfWeekIntSerializer());
	}

	private static class DayOfWeekIntSerializer
		implements Serializer<DayOfWeek>
	{
		@Override
		public DayOfWeek read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return DayOfWeek.of(in.readInt());
		}

		@Override
		public void write(DayOfWeek object, StreamingOutput out)
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
			return obj instanceof DayOfWeekIntSerializer;
		}

		@Override
		public String toString()
		{
			return "DayOfWeekIntSerializer{}";
		}
	}
}
