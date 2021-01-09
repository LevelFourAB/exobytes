package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.time.TemporalHints.StandardFormat;

/**
 * Serializers for {@link LocalTime}.
 */
public class LocalTimeSerializers
	extends TemporalSerializers<LocalTime>
{
	public LocalTimeSerializers()
	{
		super(
			LocalTime.class,
			DateTimeFormatter.ISO_LOCAL_TIME,
			Sets.immutable.of(StandardFormat.ISO_LOCAL_TIME),
			millis -> LocalTime.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC),
			object -> object.toEpochSecond(LocalDate.EPOCH, ZoneOffset.UTC) * 1000,
			LocalTime::from
		);
	}
}
