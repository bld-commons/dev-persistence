/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.LikeString.java
 */
package bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.reflection.type.LikeType;

/**
 * The Interface LikeString.
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface LikeString {
	
	/**
	 * Like type.
	 *
	 * @return the like type
	 */
	public LikeType likeType() default LikeType.LEFT_RIGHT; 
	
	/**
	 * Ignore case.
	 *
	 * @return true, if successful
	 */
	public boolean ignoreCase() default true;
	
}
