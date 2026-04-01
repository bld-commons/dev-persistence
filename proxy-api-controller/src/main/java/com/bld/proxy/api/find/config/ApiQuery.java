package com.bld.proxy.api.find.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies an explicit query string to be executed by the
 * {@code proxy-api-controller} interceptor instead of relying on the
 * automatic JPQL generation.
 *
 * <p>When this annotation is absent the framework generates a JPQL query
 * dynamically from the method's {@link com.bld.proxy.api.find.annotations.ApiFind}
 * binding and the parameters present in the request. When {@code @ApiQuery} is
 * present, its {@link #value()} is used as-is (either as a native SQL or JPQL
 * string depending on the {@link #jpql()} flag).</p>
 *
 * <p><b>Native SQL example</b></p>
 * <pre>{@code
 * @GetMapping("/products/custom")
 * @ApiQuery("SELECT * FROM product WHERE active = true")
 * List<ProductDto> findActive();
 * }</pre>
 *
 * <p><b>JPQL example</b></p>
 * <pre>{@code
 * @GetMapping("/products/custom")
 * @ApiQuery(value = "SELECT p FROM Product p WHERE p.active = true", jpql = true)
 * List<ProductDto> findActive();
 * }</pre>
 *
 * @author Francesco Baldi
 * @see DefaultOrderBy
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiQuery {

	/**
	 * The query string to execute. For native SQL queries this must be a valid
	 * SQL statement; for JPQL queries ({@link #jpql()} = {@code true}) it must
	 * be a valid JPQL statement.
	 *
	 * @return the query string; must not be blank when {@link #jpql()} is {@code false}
	 */
	public String value();

	/**
	 * When {@code true}, the {@link #value()} is treated as a JPQL query and
	 * the automatic query-building pipeline is used. When {@code false} (default),
	 * {@link #value()} is executed as a native SQL query.
	 *
	 * @return {@code true} for JPQL mode, {@code false} for native SQL mode
	 */
	public boolean jpql() default false;

	/**
	 * Default ordering to apply when the caller has not specified a sort key.
	 * Each entry maps to one {@code ORDER BY} clause item.
	 *
	 * @return the default order-by clauses; empty array means no default ordering
	 */
	public DefaultOrderBy[] orderBy() default {};

}
