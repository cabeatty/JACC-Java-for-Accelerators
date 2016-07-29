package segmm.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS)
//@Target(ElementType.TYPE)
public @interface targetData
{
	String[] value() default {};
}
