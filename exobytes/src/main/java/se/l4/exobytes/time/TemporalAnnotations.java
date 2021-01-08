package se.l4.exobytes.time;

import java.lang.annotation.Annotation;
import java.time.temporal.ChronoUnit;

/**
 * Annotations related to {@link se.l4.exobytes.Serializer}s for time.
 */
public class TemporalAnnotations
{
	private static final Temporal.Timestamp TIMESTAMP = new Temporal.Timestamp()
	{
		@Override
		public Class<? extends Annotation> annotationType()
		{
			return Temporal.Timestamp.class;
		}

		@Override
		public String toString()
		{
			return "@Temporal.Timestamp";
		}
	};

	private TemporalAnnotations()
	{
	}

	public static Temporal.Precision precision(ChronoUnit unit)
	{
		return new Temporal.Precision()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Temporal.Precision.class;
			}

			@Override
			public ChronoUnit value()
			{
				return unit;
			}

			@Override
			public String toString()
			{
				return "@Temporal.Precision(value=" + value() + ")";
			}
		};
	}

	/**
	 * Get an annotation that represents
	 */
	public static Temporal.Timestamp timestamp()
	{
		return TIMESTAMP;
	}

	public static Temporal.Format format()
	{
		return format(Temporal.StandardFormat.DEFAULT);
	}

	public static Temporal.Format format(Temporal.StandardFormat type)
	{
		return new Temporal.Format()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Temporal.Format.class;
			}

			@Override
			public Temporal.StandardFormat value()
			{
				return type;
			}

			@Override
			public String toString()
			{
				return "@Temporal.Format(value=" + value() + ")";
			}
		};
	}

	public static Temporal.CustomFormat customFormat(String format)
	{
		return new Temporal.CustomFormat()
		{
			@Override
			public Class<? extends Annotation> annotationType()
			{
				return Temporal.CustomFormat.class;
			}

			@Override
			public String value()
			{
				return format;
			}

			@Override
			public String toString()
			{
				return "@Temporal.CustomFormat(value=" + value() + ")";
			}
		};
	}
}
