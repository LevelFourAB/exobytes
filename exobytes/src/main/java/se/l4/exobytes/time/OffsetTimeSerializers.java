package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;

public class OffsetTimeSerializers
	implements SerializerResolver<OffsetTime>
{
	@Override
	public Optional<? extends SerializerOrResolver<OffsetTime>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(OffsetTime.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<OffsetTime>> result;

		result = TemporalSerializers.findTimestampSerializer(
			encounter,
			millis -> OffsetTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC),
			object -> object.toEpochSecond(LocalDate.EPOCH) * 1000l
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
					return OffsetTime.ofInstant(Instant.from(temporal), offset);
				}
				else
				{
					LocalTime time = LocalTime.from(temporal);
					return OffsetTime.of(time, offset);
				}
			},
			DateTimeFormatter.ISO_TIME,
			Sets.immutable.of(
				TemporalHints.StandardFormat.DEFAULT,
				TemporalHints.StandardFormat.ISO_DATE_TIME,
				TemporalHints.StandardFormat.ISO_TIME,
				TemporalHints.StandardFormat.ISO_LOCAL_DATE_TIME,
				TemporalHints.StandardFormat.ISO_LOCAL_TIME,
				TemporalHints.StandardFormat.ISO_OFFSET_DATE_TIME,
				TemporalHints.StandardFormat.ISO_OFFSET_TIME,
				TemporalHints.StandardFormat.RFC_1123_DATE_TIME
			)
		);

		if(result.isPresent())
		{
			return result;
		}

		return Optional.empty();
	}
}
