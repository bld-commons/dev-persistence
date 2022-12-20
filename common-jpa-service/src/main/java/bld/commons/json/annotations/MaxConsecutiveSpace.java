/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.json.annotations.MaxConsecutiveSpace.java
 */
package bld.commons.json.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bld.commons.reflection.annotations.deserialize.MaxConsecutiveSpaceDeserializer;
import bld.commons.reflection.type.UpperLowerType;

/**
 * The Interface MaxConsecutiveSpace.
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
@JacksonAnnotationsInside
@JsonDeserialize(using = MaxConsecutiveSpaceDeserializer.class)
@JsonInclude(Include.NON_NULL)
public @interface MaxConsecutiveSpace {

	/**
	 * Removes the all space type.
	 *
	 * @return true, if successful
	 */
	public boolean removeAllSpaceType() default false;
	
	/**
	 * Consecutive.
	 *
	 * @return the int
	 */
	public int consecutive() default 1;
	
	/**
	 * Trim.
	 *
	 * @return true, if successful
	 */
	public boolean trim() default true;
	
	/**
	 * Removes the endline.
	 *
	 * @return true, if successful
	 */
	public boolean removeEndline() default false;
	
	
	/**
	 * Upper lower type.
	 *
	 * @return the upper lower type
	 */
	public UpperLowerType upperLowerType() default UpperLowerType.NONE;
	
}
