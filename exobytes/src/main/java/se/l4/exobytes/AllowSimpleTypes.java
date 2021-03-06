package se.l4.exobytes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.exobytes.standard.SimpleTypeSerializer;

/**
 * Indicate that a field may contain any simple type, which is all primitive
 * types and {@link String}, the field may not contain any object.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
@Use(SimpleTypeSerializer.class)
public @interface AllowSimpleTypes
{
}
