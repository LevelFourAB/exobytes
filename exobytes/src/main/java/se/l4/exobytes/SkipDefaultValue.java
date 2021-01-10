package se.l4.exobytes;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicate that a field should not be written to the output if it is the
 * types default value. This annotation is handled correctly if a class uses
 * {@link AnnotationSerialization}.
 *
 * <p>
 * Example:
 *
 * <pre>
 * {@literal @}AnnotationSerialization
 * public class PersonData {
 *   {@literal @}Expose
 *   private final String name;
 *
 *   {@literal @}Expose
 *   {@literal @}SkipDefaultValue
 *   private final String title;
 *
 *   public PersonData({@literal @}Expose("name") String name, {@literal @}Expose("title") String title) {
 *     this.name = name;
 *     this.title = title;
 *   }
 *
 *   // ... getters and other code here ...
 * }
 *
 * // This object will write the key `title` with the value `Engineer`
 * new PersonData("Emma Smith", "Engineer");
 *
 * // This object will skip the key `title` entirely
 * new PersonData("John Smith", null);
 * </pre>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
@Documented
public @interface SkipDefaultValue
{

}
