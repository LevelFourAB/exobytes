package se.l4.exobytes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import se.l4.exobytes.internal.reflection.ReflectionSerializer;

/**
 * Annotation that can be used to activate reflection based serializing for
 * a type.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
@Documented
@Use(ReflectionSerializer.class)
public @interface AnnotationSerialization
{
}
