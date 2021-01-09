package se.l4.exobytes.time;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
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
 * Serializers for {@link Duration}.
 */
public class DurationSerializers
	implements SerializerResolver<Duration>
{
	@Override
	public Optional<? extends SerializerOrResolver<Duration>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(Duration.class))
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

			return Optional.of(new DurationStringSerializer());
		}

		// Serializing as a timestamp
		ChronoUnit precision = encounter.getHint(TemporalHints.Precision.class)
			.map(p -> p.value())
			.orElse(ChronoUnit.MILLIS);

		if(precision == ChronoUnit.NANOS || precision == ChronoUnit.MICROS)
		{
			throw new SerializationException("The max supported precision is milliseconds");
		}

		return Optional.of(new DurationLongSerializer(precision));
	}

	static class DurationLongSerializer
		implements Serializer<Duration>
	{
		private final ChronoUnit precision;

		public DurationLongSerializer(
			ChronoUnit precision
		)
		{
			this.precision = precision;
		}

		@Override
		public void write(Duration object, StreamingOutput out)
			throws IOException
		{
			out.writeLong(object.toMillis() / precision.getDuration().toMillis());
		}

		@Override
		public Duration read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return Duration.ofMillis(in.readLong() * precision.getDuration().toMillis());
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(precision);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			DurationLongSerializer other = (DurationLongSerializer) obj;
			return precision == other.precision;
		}

		@Override
		public String toString()
		{
			return "DurationLongSerializer{precision=" + precision + "}";
		}
	}

	private static class DurationStringSerializer
		implements Serializer<Duration>
	{
		@Override
		public Duration read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);
			return Duration.parse(in.readString());
		}

		@Override
		public void write(Duration object, StreamingOutput out)
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
			return obj instanceof DurationStringSerializer;
		}

		@Override
		public String toString()
		{
			return "DurationStringSerializer{}";
		}
	}
}
