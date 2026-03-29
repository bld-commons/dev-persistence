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
 * Marks a {@link String} field in a {@link com.bld.commons.reflection.model.BaseParameter}
 * subclass as a LIKE filter.
 *
 * <p>The reflection engine wraps the field value with SQL {@code %} wildcards according
 * to the chosen {@link LikeType}, and optionally converts the value to upper- or
 * lower-case before binding it as a JPQL parameter.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * public class PersonFilter extends BaseParameter {
 *
 *     // generates: AND LOWER(e.lastName) LIKE LOWER(:lastName)  →  '%smith%'
 *     @LikeString(likeType = LikeType.LEFT_RIGHT, upperLowerType = UpperLowerType.LOWER)
 *     private String lastName;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see LikeType
 * @see com.bld.commons.utils.types.UpperLowerType
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface LikeString {

	/**
	 * Determines where the wildcard {@code %} is placed relative to the value.
	 * Defaults to {@link LikeType#LEFT_RIGHT} ({@code '%value%'}).
	 *
	 * @return the LIKE wildcard placement strategy
	 */
	public LikeType likeType() default LikeType.LEFT_RIGHT;

	/**
	 * Optional case transformation applied to both the parameter value and the
	 * database column before comparison. Defaults to {@code NONE} (no transformation).
	 *
	 * @return the case-transformation strategy
	 */
	public UpperLowerType upperLowerType() default UpperLowerType.NONE;

}
