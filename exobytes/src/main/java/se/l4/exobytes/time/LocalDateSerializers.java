package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.time.TemporalHints.StandardFormat;

/**
 * Serializers for {@link LocalDate}.
 */
public class LocalDateSerializers
	extends TemporalSerializers<LocalDate>
{
	public LocalDateSerializers()
	{
		super(
			LocalDate.class,
			DateTimeFormatter.ISO_LOCAL_DATE,
			Sets.immutable.of(StandardFormat.ISO_LOCAL_DATE),
			millis -> LocalDate.ofInstant(Instant.ofEpochMilli(millis), ZoneOffset.UTC),
			object -> object.toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC) * 1000,
			LocalDate::from
		);
	}
}
