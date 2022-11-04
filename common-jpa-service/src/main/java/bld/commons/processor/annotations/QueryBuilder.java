/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.QueryBuilder.java
 */
package bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface QueryBuilder.
 */
@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
public @interface QueryBuilder {
	
	public boolean distinct() default true;

	/**
	 * Joins.
	 *
	 * @return the string[]
	 */
	public String[] joins() default {};

	/**
	 * Conditions.
	 *
	 * @return the condition builder[]
	 */
	public ConditionBuilder[] conditions() default {};

	/**
	 * Custom conditions.
	 *
	 * @return the custom condition builder[]
	 */
	public CustomConditionBuilder[] customConditions() default {};

	/**
	 * Custom native conditions.
	 *
	 * @return the custom condition builder[]
	 */
	public CustomConditionBuilder[] customNativeConditions() default {};

	public JpqlOrderBuilder[] jpaOrder() default {};
	
	public NativeOrderBuilder[] nativeOrder() default {};

}
