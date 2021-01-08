package se.l4.exobytes.time;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Objects;
import java.util.Optional;
import java.util.function.LongFunction;
import java.util.function.ToLongFunction;

import org.eclipse.collections.api.set.ImmutableSet;
import org.eclipse.collections.api.set.SetIterable;

import se.l4.exobytes.SerializationException;
import se.l4.exobytes.Serializer;
import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;
import se.l4.exobytes.streaming.StreamingInput;
import se.l4.exobytes.streaming.StreamingOutput;
import se.l4.exobytes.streaming.Token;
import se.l4.exobytes.time.Temporal.StandardFormat;

/**
 * {@link Serializer} implementations that handle {@link TemporalAccessor}.
 */
public class TemporalSerializers<T extends TemporalAccessor>
	implements SerializerResolver<T>
{
	private final Class<T> type;
	private final DateTimeFormatter defaultFormatter;
	private final ImmutableSet<StandardFormat> formatsSupported;

	private final LongFunction<T> millisToObject;
	private final ToLongFunction<T> objectToMillis;
	private final TemporalQuery<T> temporalQuery;

	public TemporalSerializers(
		Class<T> type,
		DateTimeFormatter defaultFormatter,
		ImmutableSet<Temporal.StandardFormat> formatsSupported,
		LongFunction<T> millisToObject,
		ToLongFunction<T> objectToMillis,
		TemporalQuery<T> temporalQuery
	)
	{
		this.type = type;

		this.defaultFormatter = defaultFormatter;
		this.formatsSupported = formatsSupported;

		this.millisToObject = millisToObject;
		this.objectToMillis = objectToMillis;
		this.temporalQuery = temporalQuery;
	}

	@Override
	public Optional<? extends SerializerOrResolver<T>> find(TypeEncounter encounter)
	{
		if(! encounter.getType().isErasedType(type))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<T>> result;

		result = findTimestampSerializer(encounter, millisToObject, objectToMillis);
		if(result.isPresent())
		{
			return result;
		}

		result = findFormattingSerializer(encounter, temporalQuery, defaultFormatter, formatsSupported);
		if(result.isPresent())
		{
			return result;
		}

		return Optional.empty();
	}

	protected Optional<? extends SerializerOrResolver<T>> find0(TypeEncounter encounter)
	{
		return Optional.empty();
	}

	/**
	 * Resolve a serializer if {@link Temporal.Timestamp} is present.
	 *
	 * @param <T>
	 * @param encounter
	 * @param millisToObject
	 * @param objectToMillis
	 * @return
	 */
	public static <T extends TemporalAccessor> Optional<? extends SerializerOrResolver<T>> findTimestampSerializer(
		TypeEncounter encounter,
		LongFunction<T> millisToObject,
		ToLongFunction<T> objectToMillis
	)
	{
		if(encounter.getHint(Temporal.Timestamp.class).isPresent())
		{
			// Serializing as a timestamp
			ChronoUnit precision = encounter.getHint(Temporal.Precision.class)
				.map(p -> p.value())
				.orElse(ChronoUnit.MILLIS);

			if(precision == ChronoUnit.NANOS || precision == ChronoUnit.MICROS)
			{
				throw new SerializationException("The max supported precision of a timestamp is milliseconds");
			}

			return Optional.of(new TimestampImpl<>(millisToObject, objectToMillis, precision));
		}

		return Optional.empty();
	}

	public static <T extends TemporalAccessor> Optional<? extends SerializerOrResolver<T>> findFormattingSerializer(
		TypeEncounter encounter,
		TemporalQuery<T> temporalQuery,
		DateTimeFormatter defaultFormatter,
		SetIterable<Temporal.StandardFormat> formatsSupported
	)
	{
		Optional<Temporal.Format> format = encounter.getHint(Temporal.Format.class);
		if(format.isPresent())
		{
			// Formatting as a known string
			Temporal.StandardFormat sf = format.get().value();
			if(sf != Temporal.StandardFormat.DEFAULT && ! formatsSupported.contains(sf))
			{
				throw new SerializationException("The formatter " + sf + " is not supported");
			}

			DateTimeFormatter formatter;
			switch(sf)
			{
				case DEFAULT:
					formatter = defaultFormatter;
					break;
				case ISO_DATE:
					formatter = DateTimeFormatter.ISO_DATE;
					break;
				case ISO_DATE_TIME:
					formatter = DateTimeFormatter.ISO_DATE_TIME;
					break;
				case ISO_INSTANT:
					formatter = DateTimeFormatter.ISO_INSTANT;
					break;
				case ISO_LOCAL_DATE:
					formatter = DateTimeFormatter.ISO_LOCAL_DATE;
					break;
				case ISO_LOCAL_DATE_TIME:
					formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
					break;
				case ISO_LOCAL_TIME:
					formatter = DateTimeFormatter.ISO_LOCAL_TIME;
					break;
				case ISO_OFFSET_DATE:
					formatter = DateTimeFormatter.ISO_OFFSET_DATE;
					break;
				case ISO_OFFSET_DATE_TIME:
					formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
					break;
				case ISO_OFFSET_TIME:
					formatter = DateTimeFormatter.ISO_OFFSET_TIME;
					break;
				case ISO_ORDINAL_DATE:
					formatter = DateTimeFormatter.ISO_ORDINAL_DATE;
					break;
				case ISO_TIME:
					formatter = DateTimeFormatter.ISO_TIME;
					break;
				case ISO_WEEK_DATE:
					formatter = DateTimeFormatter.ISO_WEEK_DATE;
					break;
				case ISO_ZONED_DATE_TIME:
					formatter = DateTimeFormatter.ISO_ZONED_DATE_TIME;
					break;
				case BASIC_ISO_DATE:
					formatter = DateTimeFormatter.BASIC_ISO_DATE;
					break;
				case RFC_1123_DATE_TIME:
					formatter = DateTimeFormatter.RFC_1123_DATE_TIME;
					break;
				default:
					throw new SerializationException("Unknown format " + sf + "; Please report this error");
			}

			return Optional.of(new FormattingImpl<>(temporalQuery, formatter));
		}

		Optional<Temporal.CustomFormat> customFormat = encounter.getHint(Temporal.CustomFormat.class);
		if(customFormat.isPresent())
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(customFormat.get().value());
			return Optional.of(
				new FormattingImpl<>(temporalQuery, formatter)
			);
		}

		return Optional.empty();
	}

	static final class TimestampImpl<T extends TemporalAccessor>
		implements Serializer<T>
	{
		private final LongFunction<T> millisToObject;
		private final ToLongFunction<T> objectToMillis;
		private final ChronoUnit precision;

		public TimestampImpl(
			LongFunction<T> millisToObject,
			ToLongFunction<T> objectToMillis,
			ChronoUnit precision
		)
		{
			this.millisToObject = millisToObject;
			this.objectToMillis = objectToMillis;
			this.precision = precision;
		}

		@Override
		public void write(T object, StreamingOutput out)
			throws IOException
		{
			long millis = objectToMillis.applyAsLong(object);

			if(precision == ChronoUnit.MILLIS)
			{
				out.writeLong(millis);
			}
			else
			{
				out.writeLong(millis / precision.getDuration().toMillis());
			}
		}

		@Override
		public T read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);

			long value = in.readLong();
			if(precision == ChronoUnit.MILLIS)
			{
				return millisToObject.apply(value);
			}
			else
			{
				return millisToObject.apply(value * precision.getDuration().toMillis());
			}
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(millisToObject, objectToMillis, precision);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			TimestampImpl other = (TimestampImpl) obj;
			return Objects.equals(millisToObject, other.millisToObject)
				&& Objects.equals(objectToMillis, other.objectToMillis)
				&& precision == other.precision;
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{precision=" + precision + "}";
		}
	}

	static final class FormattingImpl<T extends TemporalAccessor>
		implements Serializer<T>
	{
		private final TemporalQuery<T> temporalQuery;
		private final DateTimeFormatter formatter;

		public FormattingImpl(
			TemporalQuery<T> temporalQuery,
			DateTimeFormatter formatter
		)
		{
			this.temporalQuery = temporalQuery;
			this.formatter = formatter;
		}

		@Override
		public void write(T object, StreamingOutput out)
			throws IOException
		{
			out.writeString(formatter.format(object));
		}

		@Override
		public T read(StreamingInput in)
			throws IOException
		{
			in.next(Token.VALUE);

			String value = in.readString();
			return formatter.parse(value, temporalQuery);
		}

		@Override
		public int hashCode()
		{
			return Objects.hash(formatter, temporalQuery);
		}

		@Override
		public boolean equals(Object obj)
		{
			if(this == obj) return true;
			if(obj == null) return false;
			if(getClass() != obj.getClass()) return false;
			FormattingImpl other = (FormattingImpl) obj;
			return Objects.equals(formatter, other.formatter)
				&& Objects.equals(temporalQuery, other.temporalQuery);
		}

		@Override
		public String toString()
		{
			return getClass().getSimpleName() + "{formatter=" + formatter + "}";
		}
	}
}
