package se.l4.exobytes.time;

import java.time.MonthDay;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.Optional;

import org.eclipse.collections.api.factory.Sets;

import se.l4.exobytes.SerializerOrResolver;
import se.l4.exobytes.SerializerResolver;
import se.l4.exobytes.TypeEncounter;

/**
 * Serializers for {@link MonthDay}.
 */
public class MonthDaySerializers
	implements SerializerResolver<MonthDay>
{
	private static final DateTimeFormatter FORMATTER = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendLiteral('-')
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.toFormatter();

	@Override
	public Optional<? extends SerializerOrResolver<MonthDay>> find(
		TypeEncounter encounter
	)
	{
		if(! encounter.getType().isErasedType(MonthDay.class))
		{
			return Optional.empty();
		}

		Optional<? extends SerializerOrResolver<MonthDay>> result =
			TemporalSerializers.findFormattingSerializer(
				encounter,
				MonthDay::from,
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
