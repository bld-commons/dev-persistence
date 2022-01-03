/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.ConditionBuilder.java
 */
package bld.commons.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import bld.commons.processor.OperationType;
import bld.commons.reflection.type.UpperLowerType;

/**
 * The Interface ConditionBuilder.
 */
@Retention(RetentionPolicy.CLASS)
public @interface ConditionBuilder {

	/**
	 * Field.
	 *
	 * @return the string
	 */
	public String field();
	
	/**
	 * Operation.
	 *
	 * @return the operation type
	 */
	public OperationType operation();
	
	/**
	 * Parameter.
	 *
	 * @return the string
	 */
	public String parameter();
	
	/**
	 * Upper lower.
	 *
	 * @return the upper lower type
	 */
	public UpperLowerType upperLower() default UpperLowerType.NONE;
	
	
	public boolean nullable() default false;
	
	
}
