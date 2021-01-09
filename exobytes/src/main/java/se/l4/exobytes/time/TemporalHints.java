package se.l4.exobytes.time;

import java.lang.annotation.Annotation;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * Hints related to serialization of {@code java.time} objects.
 */
public class TemporalHints
{
	private TemporalHints()
	{
	}

	/**
	 * Indicate that the value should be written as a timestamp. When this is used
	 * the serialization format will be a {@code long} with a precision given by
	 * {@link Precision}.
	 *
	 * Example usage:
	 *
	 * <pre>
	 * {@literal @}Expose
	 * {@literal @}TemporalHints.Timestamp
	 * public Instant created;
	 * </pre>
	 */
	public @interface Timestamp
	{
	}

	/**
	 * Adjust the precision of a {@link java.time.temporal.Temporal} that can be serialized.
	 *
	 * <pre>
	 * {@literal @}Expose
	 * {@literal @}TemporalHints.Timestamp
	 * {@literal @}TemporalHints.Precision(ChronoUnit.SECONDS)
	 * public Instant created;
	 * </pre>
	 */
	public @interface Precision
	{
		/**
		 * The precision to use.
		 *
		 * @return
		 */
		ChronoUnit value();
	}

	/**
	 * Indicate that a {@link java.time.temporal.Temporal} value should be
	 * serialized using a string format. Without a value this annotation will
	 * activate the best format based on the underlying type but another
	 * formatter may be picked from {@link StandardFormat}.
	 *
	 * <p>
	 * If you want to use a custom format use {@link CustomFormat} instead.
	 */
	public @interface Format
	{
		/**
		 * The standard format to be used.
		 *
		 * @return
		 */
		StandardFormat value() default StandardFormat.DEFAULT;
	}

	/**
	 * Indicate that this temporal value should use a custom string format.
	 */
	public @interface CustomFormat
	{
		/**
		 * The format to use for this temporal value. This format will be used
		 * with {@link DateTimeFormatter}, see its documentation for details
		 * on the supported pattern.
		 */
		String value();
	}

	/**
	 * Standard formats supported. Based on the fields available in
	 * {@link DateTimeFormatter}.
	 */
	public enum StandardFormat
	{
		/**
		 * Automatically determine the best formatter to use.
		 */
		DEFAULT,

		/**
		 * Use {@link DateTimeFormatter#ISO_LOCAL_DATE} to format as an ISO
		 * date such as {@code 2011-12-03}.
		 */
		ISO_LOCAL_DATE,

		/**
		 * Use {@link DateTimeFormatter#ISO_OFFSET_DATE} to format as an
		 * ISO date with offset such as {@code 2011-12-03+01:00}.
		 */
		ISO_OFFSET_DATE,

		/**
		 * Use {@link DateTimeFormatter#ISO_DATE} to format as an ISO date
		 * with an offset if available, such as {@code 2011-12-03} or
		 * {@code 2011-12-03+01:00}.
		 */
		ISO_DATE,

		/**
		 * Use {@link DateTimeFormatter#ISO_LOCAL_TIME} to format as an ISO
		 * time, such as {@code 10:15} or {@code 10:15:30}.
		 */
		ISO_LOCAL_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_OFFSET_TIME} to format as an ISO
		 * time with an offset, such as {@code 10:15+01:00} or
		 * {@code 10:15:30+01:00}.
		 */
		ISO_OFFSET_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_TIME} to format as an ISO time
		 * time and offset if available, such as {@code 10:15}, {@code 10:15:30}
		 * or {@code 10:15:30+01:00}.
		 */
		ISO_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME} to format as an
		 * ISO date-time, such as {@code 2011-12-03T10:15:30}.
		 */
		ISO_LOCAL_DATE_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME} to format as an
		 * ISO date-time with an offset, such as {@code 2011-12-03T10:15:30+01:00}.
		 */
		ISO_OFFSET_DATE_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_ZONED_DATE_TIME} to format as an
		 * ISO-like date-time with an offset and zone, such as
		 * {@code 2011-12-03T10:15:30+01:00[Europe/Paris]}.
		 */
		ISO_ZONED_DATE_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_DATE_TIME} to format as an ISO-like
		 * date-time with an offset and zone if available, such as
		 * {@code 2011-12-03T10:15:30}, {@code 2011-12-03T10:15:30+01:00} or
		 * {@code 2011-12-03T10:15:30+01:00[Europe/Paris]}.
		 */
		ISO_DATE_TIME,

		/**
		 * Use {@link DateTimeFormatter#ISO_ORDINAL_DATE} to format as an
		 * ordinal date, such as {@code 2012-337}.
		 */
		ISO_ORDINAL_DATE,

		/**
		 * Use {@link DateTimeFormatter#ISO_WEEK_DATE} to format as a week-based
		 * date, such as {@code 2012-W48-6}.
		 */
		ISO_WEEK_DATE,

		/**
		 * Use {@link DateTimeFormatter#ISO_INSTANT} to format as an instant in
		 * UTC, such as {@code 2011-12-03T10:15:30Z}.
		 */
		ISO_INSTANT,

		/**
		 * Use {@link DateTimeFormatter#BASIC_ISO_DATE} to format as a date,
		 * such as @{code 20111203}.
		 */
		BASIC_ISO_DATE,

		/**
		 * Use {@link DateTimeFormatter#RFC_1123_DATE_TIME} to format as an
		 * RFC-1123 date-time, such as {@code Tue, 3 Jun 2008 11:05:30 GMT}.
		 */
		RFC_1123_DATE_TIME
	}

	private static final TemporalHints.Timestamp TIMESTAMP = new TemporalHints.Timestamp()
	{
		@Override
		public Class<? extends Annotation> annotationType()
		{
			return TemporalHints.Timestamp.class;
		}

		@Override
		public String toString()
		{
			return "@TemporalHints.Timestamp";
		}
	};

	public static TemporalHints.Precision precision(ChronoUnit unit)
	{
		return new TemporalHints.Precision()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return TemporalHints.Precision.class;
			}

			@Override
			public ChronoUnit value()
			{
				return unit;
			}

			@Override
			public String toString()
			{
				return "@TemporalHints.Precision(value=" + value() + ")";
			}
		};
	}

	/**
	 * Get an annotation that represents
	 */
	public static TemporalHints.Timestamp timestamp()
	{
		return TIMESTAMP;
	}

	public static TemporalHints.Format format()
	{
		return format(TemporalHints.StandardFormat.DEFAULT);
	}

	public static TemporalHints.Format format(TemporalHints.StandardFormat type)
	{
		return new TemporalHints.Format()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return TemporalHints.Format.class;
			}

			@Override
			public TemporalHints.StandardFormat value()
			{
				return type;
			}

			@Override
			public String toString()
			{
				return "@TemporalHints.Format(value=" + value() + ")";
			}
		};
	}

	public static CustomFormat customFormat(String format)
	{
		return new TemporalHints.CustomFormat()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return TemporalHints.CustomFormat.class;
			}

			@Override
			public String value()
			{
				return format;
			}

			@Override
			public String toString()
			{
				return "@TemporalHints.CustomFormat(value=" + value() + ")";
			}
		};
	}
}
