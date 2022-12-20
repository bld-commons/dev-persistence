/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.OrderAlias.java 
 */
package bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface OrderAlias.
 */
@Retention(CLASS)
@Target(ANNOTATION_TYPE)
public @interface OrderAlias {

	/**
	 * Alias.
	 *
	 * @return the string
	 */
	public String alias();
	
	/**
	 * Field.
	 *
	 * @return the string
	 */
	public String field();
}
