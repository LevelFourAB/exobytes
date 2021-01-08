package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;

/**
 * Serializers for {@link OffsetDateTime}
 */
public class OffsetDateTimeSerializers
	implements SerializerResolver<OffsetDateTime>
{
	@Override
	public Optional<? extends SerializerOrResolver<OffsetDateTime>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(OffsetDateTime.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<OffsetDateTime>> result;

		result = TemporalSerializers.findTimestampSerializer(
			encounter,
			millis -> OffsetDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC),
			object -> object.toInstant().toEpochMilli()
		);

		if(result.isPresent())
		{
			return result;
		}

		result = TemporalSerializers.findFormattingSerializer(
			encounter,
			temporal -> {
				/*
				 * This is a lenient converter that will fill in defaults if
				 * something isn't parsed.
				 */
				ZoneOffset offset = temporal.query(TemporalQueries.offset());
				if(offset == null)
				{
					offset = ZoneOffset.UTC;
				}

				if(temporal.isSupported(ChronoField.INSTANT_SECONDS))
				{
					return Instant.from(temporal).atOffset(offset);
				}
				else
				{
					LocalDate date = temporal.query(TemporalQueries.localDate());
					if(date == null)
					{
						date = LocalDate.now(ZoneOffset.UTC);
					}

					LocalTime time = temporal.query(TemporalQueries.localTime());
					if(time == null)
					{
						time = LocalTime.MIDNIGHT;
					}

					return OffsetDateTime.of(date, time, offset);
				}
			},
			DateTimeFormatter.ISO_DATE_TIME,
			Sets.immutable.of(
				Temporal.StandardFormat.DEFAULT,
				Temporal.StandardFormat.ISO_DATE,
				Temporal.StandardFormat.ISO_DATE_TIME,
				Temporal.StandardFormat.ISO_TIME,
				Temporal.StandardFormat.ISO_LOCAL_DATE,
				Temporal.StandardFormat.ISO_LOCAL_DATE_TIME,
				Temporal.StandardFormat.ISO_LOCAL_TIME,
				Temporal.StandardFormat.ISO_OFFSET_DATE,
				Temporal.StandardFormat.ISO_OFFSET_DATE_TIME,
				Temporal.StandardFormat.ISO_OFFSET_TIME,
				Temporal.StandardFormat.ISO_ORDINAL_DATE,
				Temporal.StandardFormat.RFC_1123_DATE_TIME
			)
		);

		if(result.isPresent())
		{
			return result;
		}

		return Optional.empty();
	}
}
