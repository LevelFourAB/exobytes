package se.l4.exobytes.time;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;

public class YearSerializer
	implements SerializerResolver<Year>
{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu");

	@Override
	public Optional<? extends SerializerOrResolver<Year>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(Year.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<Year>> result;

		result = TemporalSerializers.findTimestampSerializer(
			encounter,
			millis -> Year.of(LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC).getYear()),
			year -> year.atDay(1).toEpochDay() * 86400000l
		);

		if(result.isPresent())
		{
			return result;
		}

		result = TemporalSerializers.findFormattingSerializer(
			encounter,
			Year::from,
			FORMATTER,
			Sets.immutable.of(Temporal.StandardFormat.DEFAULT)
		);

		if(result.isPresent())
		{
			return result;
		}

		return Optional.of(new YearIntSerializer());
	}

	private static class YearIntSerializer
		implements Serializer<Year>
	{
		@Override
		public Year read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return Year.of(in.readInt());
		}

		@Override
		public void write(Year object, StreamingOutput out)
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
			return obj instanceof YearIntSerializer;
		}

		@Override
		public String toString()
		{
			return "YearIntSerializer{}";
		}
	}
}
