/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.CustomConditionBuilder.java
 */
package com.bld.commons.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.bld.commons.processor.ConditionType;

/**
 * The Interface CustomConditionBuilder.
 */
@Retention(RetentionPolicy.CLASS)
public @interface CustomConditionBuilder {

	/**
	 * Parameter.
	 *
	 * @return the string
	 */
	public String parameter();
	
	/**
	 * Condition.
	 *
	 * @return the string
	 */
	public String condition();
	
	/**
	 * Type.
	 *
	 * @return the condition type
	 */
	public ConditionType type() default ConditionType.SELECT;
	
	public String[] keys() default "default";
	
}
