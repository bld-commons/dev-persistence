package bld.commons.reflection.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConditionsZone {

	public String key();
	
	public boolean initWhere() default true;
	
}
