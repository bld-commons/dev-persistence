/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.ConditionBuilder.java
 */
package bld.commons.processor.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import com.bld.commons.utils.types.UpperLowerType;

import bld.commons.processor.OperationType;

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
	
	
	/**
	 * Nullable.
	 *
	 * @return true, if successful
	 */
	public boolean nullable() default false;
	
	
}
