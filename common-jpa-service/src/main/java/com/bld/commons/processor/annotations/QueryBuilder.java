/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.QueryBuilder.java
 */
package com.bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Triggers compile-time generation of the {@link com.bld.commons.service.QueryJpql}
 * implementation for a JPA entity.
 *
 * <p>Place this annotation on the service interface (or class) associated with a
 * JPA entity. The {@code processor-jpa-service} annotation processor picks it up
 * and generates a {@code *QueryJpqlImpl} class containing pre-built JPQL strings
 * and condition/order maps.</p>
 *
 * <p>This annotation has {@link RetentionPolicy#SOURCE} retention — it is consumed
 * entirely at compile time and is not present in the compiled bytecode.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * @QueryBuilder(
 *     distinct = true,
 *     joins    = { "LEFT JOIN FETCH e.category" },
 *     conditions = {
 *         @ConditionBuilder(field = "e.name",   operation = OperationType.LIKE,   parameter = "name"),
 *         @ConditionBuilder(field = "e.active", operation = OperationType.EQUALS, parameter = "active")
 *     },
 *     jpaOrder = {
 *         @JpqlOrderBuilder(sortKey = "name", field = "e.name")
 *     }
 * )
 * public interface ProductService extends JpaService<Product, Long> { }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ConditionBuilder
 * @see CustomConditionBuilder
 * @see JpqlOrderBuilder
 * @see NativeOrderBuilder
 */
@Retention(RetentionPolicy.SOURCE)
@Target(TYPE)
public @interface QueryBuilder {

	/**
	 * Whether to add the {@code DISTINCT} keyword to the generated SELECT query.
	 * Defaults to {@code true}.
	 *
	 * @return {@code true} if the SELECT should use DISTINCT
	 */
	public boolean distinct() default true;

	/**
	 * Static JOIN / JOIN FETCH fragments always appended to the FROM clause,
	 * regardless of which filter parameters are active.
	 * For conditional joins (applied only when a parameter is present) use the
	 * one-to-many join map instead.
	 *
	 * @return array of JPQL join fragments
	 */
	public String[] joins() default {};

	/**
	 * JPQL condition definitions. Each entry maps a named parameter to a
	 * specific JPQL condition fragment that is appended to the WHERE clause
	 * when the corresponding filter parameter is non-null.
	 *
	 * @return array of JPQL condition builders
	 */
	public ConditionBuilder[] conditions() default {};

	/**
	 * Custom JPQL conditions expressed as raw JPQL fragments.
	 * Use these when the standard {@link ConditionBuilder} operations are insufficient.
	 *
	 * @return array of custom JPQL condition builders
	 */
	public CustomConditionBuilder[] customConditions() default {};

	/**
	 * Custom native SQL conditions expressed as raw SQL fragments.
	 *
	 * @return array of custom native condition builders
	 */
	public CustomConditionBuilder[] customNativeConditions() default {};

	/**
	 * Sort key definitions for JPQL queries.
	 * Each entry maps a logical sort key (used in {@link com.bld.commons.reflection.model.OrderBy})
	 * to the corresponding JPQL field expression.
	 *
	 * @return array of JPQL order builders
	 */
	public JpqlOrderBuilder[] jpaOrder() default {};

	/**
	 * Sort key definitions for native SQL queries.
	 *
	 * @return array of native order builders
	 */
	public NativeOrderBuilder[] nativeOrder() default {};

}
