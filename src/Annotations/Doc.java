package Annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)

public @interface Doc
{
	String author() default "cabeatty";
	String email() default "cabeatty@oakland.edu";
	String [] reviewers() default {};

}