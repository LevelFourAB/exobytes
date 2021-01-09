package se.l4.exobytes.time;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;

/**
 * Serializers for {@link YearMonth}.
 */
public class YearMonthSerializer
	implements SerializerResolver<YearMonth>
{
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM");

	@Override
	public Optional<? extends SerializerOrResolver<YearMonth>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(YearMonth.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<YearMonth>> result;

		result = TemporalSerializers.findTimestampSerializer(
			encounter,
			millis -> YearMonth.from(LocalDate.ofInstant(Instant.ofEpochSecond(millis / 1000l), ZoneOffset.UTC)),
			yearMonth -> yearMonth.atDay(1).toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.UTC) * 1000l
		);

		if(result.isPresent())
		{
			return result;
		}

		result = TemporalSerializers.findFormattingSerializer(
			encounter,
			YearMonth::from,
			FORMATTER,
			Sets.immutable.of(TemporalHints.StandardFormat.DEFAULT)
		);

		if(result.isPresent())
		{
			return result;
		}

		return Optional.empty();
	}
}
