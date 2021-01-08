package se.l4.exobytes.time;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.MonthDay;
import java.time.OffsetDateTime;
import java.time.OffsetTime;
import java.time.Period;
import java.time.Year;
import java.time.YearMonth;
import java.time.ZonedDateTime;

import se.l4.exobytes.Serializers;
import se.l4.exobytes.SerializersModule;

/**
 * Module that provides serializer implementations for time objects in
 * {@code java.time}.
 */
public class TimeSerializersModule
	implements SerializersModule
{
	@Override
	public void activate(Serializers serializers)
	{
		serializers.register(DayOfWeek.class, new DayOfWeekSerializers());
		serializers.register(Duration.class, new DurationSerializers());
		serializers.register(Instant.class, new InstantSerializers());
		serializers.register(LocalDate.class, new LocalDateSerializers());
		serializers.register(LocalTime.class, new LocalTimeSerializers());
		serializers.register(LocalDateTime.class, new LocalDateTimeSerializers());
		serializers.register(Month.class, new MonthSerializers());
		serializers.register(MonthDay.class, new MonthDaySerializers());
		serializers.register(OffsetDateTime.class, new OffsetDateTimeSerializers());
		serializers.register(OffsetTime.class, new OffsetTimeSerializers());
		serializers.register(Period.class, new PeriodSerializers());
		serializers.register(Year.class, new YearSerializer());
		serializers.register(YearMonth.class, new YearMonthSerializer());
		serializers.register(ZonedDateTime.class, new ZonedDateTimeSerializers());
	}
}
