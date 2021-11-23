package bld.commons.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import bld.commons.processor.OperationType;

@Retention(RetentionPolicy.CLASS)
public @interface ConditionBuilder {

	public String field();
	
	public OperationType operation();
	
	public String parameter();
	
	
}
