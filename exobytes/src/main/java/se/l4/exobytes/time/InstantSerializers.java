package se.l4.exobytes.time;

import java.time.Instant;
import java.time.format.DateTimeFormatter;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.time.TemporalHints.StandardFormat;

/**
 * Serializers for {@link Instant}.
 */
public class InstantSerializers
	extends TemporalSerializers<Instant>
{
	public InstantSerializers()
	{
		super(
			Instant.class,
			DateTimeFormatter.ISO_INSTANT,
			Sets.immutable.of(StandardFormat.ISO_INSTANT),
			Instant::ofEpochMilli,
			Instant::toEpochMilli,
			Instant::from
		);
	}
}
