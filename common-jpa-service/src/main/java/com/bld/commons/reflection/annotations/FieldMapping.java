package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Overrides the default JPQL parameter name for a field in a
 * {@link com.bld.commons.reflection.model.BaseParameter} subclass.
 *
 * <p>By default the reflection engine uses the Java field name as the named
 * parameter in the generated JPQL condition (e.g., field {@code firstName}
 * becomes {@code :firstName}). Use {@code @FieldMapping} when the JPQL
 * parameter name must differ from the Java field name.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class UserFilter extends BaseParameter {
 *
 *     // Java field is "userFirstName" but the JPQL condition uses :firstName
 *     @FieldMapping("firstName")
 *     private String userFirstName;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface FieldMapping {

	/**
	 * The JPQL named-parameter name to use instead of the Java field name.
	 *
	 * @return the parameter name; must not be blank
	 */
	public String value();
}
