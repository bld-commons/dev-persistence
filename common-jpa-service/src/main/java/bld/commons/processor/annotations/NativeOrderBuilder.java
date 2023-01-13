/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.NativeOrderBuilder.java 
 */
package bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface NativeOrderBuilder.
 */
@Retention(CLASS)
@Target(ANNOTATION_TYPE)
public @interface NativeOrderBuilder {

	/**
	 * Key.
	 *
	 * @return the string
	 */
	public String key();
	
	/**
	 * Order.
	 *
	 * @return the string
	 */
	public String order();
	
}
