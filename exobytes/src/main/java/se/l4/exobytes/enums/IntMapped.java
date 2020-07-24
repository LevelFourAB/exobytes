package se.l4.exobytes.enums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.exobytes.Use;

/**
 * Annotation that can be used to request that an {@link Enum} is serialized
 * using a specific mapping to and from integers.
 *
 * <pre>
 * {@code @}IntMapped
 * public enum EmployeeType {
 *   @IntMapped.Value(1)
 *   FULL_TIME,
 *
 *   @IntMapped.Value(2)
 *   PART_TIME;
 * }
 * </pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.TYPE,
	ElementType.TYPE_USE,
})
@Documented
@Use(EnumIntMappedSerializer.class)
public @interface IntMapped
{

	/**
	 * Define the integer an enum value should be mapped to and from.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	@Documented
	@interface Value
	{
		/**
		 * @return
		 */
		int value();
	}
}
