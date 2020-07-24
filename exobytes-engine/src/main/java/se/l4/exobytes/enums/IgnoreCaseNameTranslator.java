package se.l4.exobytes.enums;

import se.l4.exobytes.format.ValueType;

/**
 * {@link ValueTranslator} that uses the {@link Enum#name() name} of the enum.
 *
 */
public class IgnoreCaseNameTranslator
	implements ValueTranslator<String>
{
	private final Enum<?>[] values;

	public IgnoreCaseNameTranslator(Class<? extends Enum<?>> type)
	{
		values = type.getEnumConstants();
	}

	@Override
	public ValueType getType()
	{
		return ValueType.STRING;
	}

	@Override
	public String fromEnum(Enum<?> value)
	{
		return value.name();
	}

	@Override
	public Enum<?> toEnum(String value)
	{
		for(Enum<?> e : values)
		{
			if(e.name().equalsIgnoreCase(value))
			{
				return e;
			}
		}

		return null;
	}

}
