package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalQueries;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.time.TemporalHints.StandardFormat;

/**
 * Serializers for {@link ZonedDateTime}.
 */
public class ZonedDateTimeSerializers
	extends TemporalSerializers<ZonedDateTime>
{
	public ZonedDateTimeSerializers()
	{
		super(
			ZonedDateTime.class,
			DateTimeFormatter.ISO_ZONED_DATE_TIME,
			Sets.immutable.of(StandardFormat.values()),
			millis -> Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC),
			object -> 1000 * object.getLong(ChronoField.INSTANT_SECONDS)
				+ object.getLong(ChronoField.NANO_OF_SECOND) / 1000000,
			temporal -> {
				/*
				 * This is a lenient converter that will fill in defaults if
				 * something isn't parsed.
				 */
				ZoneId id = temporal.query(TemporalQueries.zone());
				if(id == null)
				{
					id = ZoneOffset.UTC;
				}

				if(temporal.isSupported(ChronoField.INSTANT_SECONDS))
				{
					return Instant.from(temporal).atZone(id);
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

					return ZonedDateTime.of(date, time, id);
				}
			}
		);
	}
}
