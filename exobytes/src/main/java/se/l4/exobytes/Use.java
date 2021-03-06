package se.l4.exobytes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark which serializer to use for a certain class.
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({
	ElementType.FIELD,
	ElementType.METHOD,
	ElementType.TYPE,
	ElementType.TYPE_USE
})
@Documented
public @interface Use
{
	/**
	 * The class to use for serialization.
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	Class<? extends SerializerOrResolver> value();
}
