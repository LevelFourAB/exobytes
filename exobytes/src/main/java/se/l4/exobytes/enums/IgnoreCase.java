package se.l4.exobytes.enums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.exobytes.Use;

/**
 * Annotation that can be used to request that an {@link Enum} is serialized
 * via its {@link Enum#name()} while also ignoring case when reading.
 *
 * <p>
 * Can be placed directly on an enum type:
 *
 * <pre>
 * {@code @}IgnoreCase
 * public enum EnumClass {
 * }
 * </pre>
 *
 * <p>
 * May also be used on fields within another class:
 *
 * <pre>
 * {@code @}IgnoreCase
 * private EnumClass type;
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
@Use(EnumIgnoreCaseNameSerializer.class)
public @interface IgnoreCase
{

}
