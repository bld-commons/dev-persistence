package bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
public @interface QueryBuilder {

	public String[] joins() default {};

	public ConditionBuilder[] conditions() default {};

	public CustomConditionBuilder[] customConditions() default {};
	
	public CustomConditionBuilder[] customNativeConditions() default {};

}
