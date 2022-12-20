/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.ConditionsZone.java 
 */
package bld.commons.reflection.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface ConditionsZone.
 */
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConditionsZone {

	/**
	 * Key.
	 *
	 * @return the string
	 */
	public String key();
	
	/**
	 * Inits the where.
	 *
	 * @return true, if successful
	 */
	public boolean initWhere() default true;
	
}
