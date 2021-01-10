package se.l4.exobytes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a certain field should be exposed in the serialized form.
 * This annotation is handled correctly if a class uses {@link AnnotationSerialization}.
 * Optionally you can control the name of the field in serialize form by
 * setting {@link #value()}, which is useful to maintain backwards compatibility
 * if you want to refactor the name used in the code but maintaining compatibility
 * with previously serialized objects.
 *
 * <p>
 * Example:
 *
 * <pre>
 * {@literal @}AnnotationSerialization
 * public class Data {
 *   {@literal @}Expose
 *   private int number;
 *
 *   // ... constructors and other code here ..
 * }
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Documented
public @interface Expose
{
	/**
	 * Get the name of the exposed value. Default is to automatically resolve
	 * this.
	 *
	 * @return
	 */
	String value() default "";
}
