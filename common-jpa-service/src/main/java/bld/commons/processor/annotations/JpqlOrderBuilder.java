package bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(CLASS)
@Target(ANNOTATION_TYPE)
public @interface JpqlOrderBuilder {

	public String key();
	
	public String order();
	
	public OrderAlias[] alias() default {};
}
