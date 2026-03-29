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
 * <p><b>Example</b></p>
 * <pre>{@code
 * @QueryBuilder(
 *     distinct = true,
 *     // dot-notation path: always generates
 *     //   join fetch product.category category
 *     joins = { "product.category" },
 *     conditions = {
 *         @ConditionBuilder(field = "product.name",   operation = OperationType.LIKE,   parameter = "name"),
 *         @ConditionBuilder(field = "product.active", operation = OperationType.EQUALS, parameter = "active")
 *     },
 *     jpaOrder = {
 *         @JpqlOrderBuilder(sortKey = "name", field = "product.name")
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
	 * Dot-notation relationship paths to eagerly join via {@code JOIN FETCH} in every
	 * query generated for this entity, regardless of which filter parameters are active.
	 *
	 * <p>Each entry is a chain of JPA relationship field names starting from the root
	 * entity alias. The processor traverses the path and generates one {@code JOIN FETCH}
	 * clause per hop, automatically deriving each alias from the field name.</p>
	 *
	 * <p>For example, given the entity alias {@code genere} and the path
	 * {@code "genere.postazioneCucina.ristorante"}, the processor generates:</p>
	 * <pre>{@code
	 * join fetch genere.postazioneCucina postazioneCucina
	 * join fetch postazioneCucina.ristorante ristorante
	 * }</pre>
	 *
	 * <p>These joins are <strong>always</strong> present in the FROM clause.
	 * For <em>conditional</em> joins — added only when the corresponding filter parameter
	 * is non-null — the processor instead generates a {@code mapOneToMany()} method that
	 * uses {@code LEFT JOIN FETCH} keyed by the filter parameter name.</p>
	 *
	 * @return array of dot-notation relationship paths to always join
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
	 * Hand-written JPQL condition fragments added to the generated condition map.
	 *
	 * <p>Use when {@link ConditionBuilder} operations (EQUALS, LIKE, IN, …) are not
	 * expressive enough — for example subqueries, {@code BETWEEN} ranges, or any
	 * JPQL expression that cannot be derived automatically from a field + operation pair.
	 * The raw {@link CustomConditionBuilder#condition()} string is inserted verbatim
	 * into {@code MAP_CONDITIONS} (or {@code MAP_DELETE_CONDITIONS} when
	 * {@link CustomConditionBuilder#type()} is {@link com.bld.commons.processor.ConditionType#DELETE})
	 * and is appended to the WHERE clause when the named parameter is non-null.</p>
	 *
	 * <p>Use the entity alias and JPA field names (not column names),
	 * exactly as you would write them in a JPQL query.</p>
	 *
	 * <p><b>Example</b></p>
	 * <pre>{@code
	 * customConditions = {
	 *     @CustomConditionBuilder(
	 *         condition = "and genere.idGenere in (:genereId)",
	 *         parameter = "genereId"
	 *         // type defaults to SELECT → MAP_CONDITIONS
	 *     )
	 * }
	 * }</pre>
	 *
	 * @return array of custom JPQL condition builders
	 */
	public CustomConditionBuilder[] customConditions() default {};

	/**
	 * Hand-written native SQL condition fragments grouped into named zones.
	 *
	 * <p>Each entry specifies the raw SQL fragment and the zone(s) it belongs to via
	 * {@link CustomConditionBuilder#keys()}. The processor generates one sub-map per
	 * zone key inside {@code MAP_NATIVE_CONDITIONS}. At runtime, each zone's conditions
	 * are collected and substituted into the SQL template through the matching
	 * {@code ${zoneName}} placeholder.</p>
	 *
	 * <p>The same condition can be registered in multiple zones by listing multiple
	 * keys, allowing reuse across different SQL templates.</p>
	 *
	 * <p><b>Example</b></p>
	 * <pre>{@code
	 * customNativeConditions = {
	 *     @CustomConditionBuilder(
	 *         condition = "and (g.id_genere, pc.id_postazione_cucina) in (:genereTuple)",
	 *         parameter = "genereTuple",
	 *         keys      = {"zone1", "zone2"}
	 *     ),
	 *     @CustomConditionBuilder(
	 *         condition = "and g.id_genere in (:idGenere)",
	 *         parameter = "idGenere",
	 *         keys      = {"zone1", "zone2"}
	 *     )
	 * }
	 * // SQL template must expose the placeholder:
	 * // SELECT g.* FROM genere g ${zone1}
	 * }</pre>
	 *
	 * @return array of custom native SQL condition builders
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
