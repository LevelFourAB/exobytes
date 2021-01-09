package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.time.TemporalHints.StandardFormat;

/**
 * Serializers for {@link LocalDateTime}.
 */
public class LocalDateTimeSerializers
	extends TemporalSerializers<LocalDateTime>
{
	public LocalDateTimeSerializers()
	{
		super(
			LocalDateTime.class,
			DateTimeFormatter.ISO_LOCAL_DATE_TIME,
			Sets.immutable.of(StandardFormat.ISO_LOCAL_DATE_TIME, StandardFormat.ISO_DATE, StandardFormat.ISO_TIME),
			millis -> LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC),
			object -> object.toInstant(ZoneOffset.UTC).toEpochMilli(),
			temporal -> {
				/*
				 * This is a lenient converter that will fill in defaults if
				 * something isn't parsed.
				 */
				if(temporal.isSupported(ChronoField.INSTANT_SECONDS))
				{
					return LocalDateTime.ofInstant(
						Instant.from(temporal),
						ZoneOffset.UTC
					);
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

					return LocalDateTime.of(date, time);
				}
			}
		);
	}
}
