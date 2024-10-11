/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.LikeString.java
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.commons.reflection.type.LikeType;
import com.bld.commons.utils.types.UpperLowerType;

/**
 * The Interface LikeString.
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface LikeString {
	
	/**
	 * Like type.
	 *
	 * @return the like type
	 */
	public LikeType likeType() default LikeType.LEFT_RIGHT; 
	

	
	/**
	 * Upper lower type.
	 *
	 * @return the upper lower type
	 */
	public UpperLowerType upperLowerType() default UpperLowerType.NONE;
	
}
