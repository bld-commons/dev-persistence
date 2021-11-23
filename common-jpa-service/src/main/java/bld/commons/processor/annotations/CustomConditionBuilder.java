package bld.commons.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import bld.commons.processor.ConditionType;

@Retention(RetentionPolicy.CLASS)
public @interface CustomConditionBuilder {

	public String parameter();
	
	public String condition();
	
	public ConditionType type() default ConditionType.SELECT;
	
}
