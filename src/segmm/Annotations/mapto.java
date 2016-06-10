/**
 * Default annotation for mem allocation, kernel config, and data copy to the gpu
 **/
package segmm.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)

public @interface mapto
{
	String[] value() default {};
}
