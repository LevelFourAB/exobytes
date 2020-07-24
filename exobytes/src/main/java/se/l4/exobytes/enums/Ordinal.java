package se.l4.exobytes.enums;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.exobytes.Use;

/**
 * Annotation that can be used to request that an {@link Enum} is serialized
 * via its {@link Enum#ordinal()}. When this is used take care to not reorder
 * enum values as it will break any serialized values.
 *
 * <p>
 * Can be placed directly on an enum type:
 *
 * <pre>
 * {@code @}Ordinal
 * public enum EnumClass {
 * }
 * </pre>
 *
 * <p>
 * May also be used on fields within another class:
 *
 * <pre>
 * {@code @}Ordinal
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
@Use(EnumOrdinalSerializer.class)
public @interface Ordinal
{
}
