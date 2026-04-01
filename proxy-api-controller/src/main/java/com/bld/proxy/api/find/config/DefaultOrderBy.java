package com.bld.proxy.api.find.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.bld.commons.reflection.type.OrderType;

/**
 * Defines a default {@code ORDER BY} clause for an {@link ApiQuery}-annotated method.
 *
 * <p>Multiple {@code @DefaultOrderBy} entries can be combined inside
 * {@link ApiQuery#orderBy()} to produce a multi-column sort. The default ordering
 * is applied only when the caller has not provided an explicit sort key via the
 * {@code sortKey} / {@code orderType} request parameters.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * @GetMapping("/products")
 * @ApiQuery(
 *     value  = "SELECT p FROM Product p",
 *     jpql   = true,
 *     orderBy = {
 *         @DefaultOrderBy(value = "p.name", orderType = OrderType.asc),
 *         @DefaultOrderBy(value = "p.price", orderType = OrderType.desc)
 *     }
 * )
 * List<ProductDto> findAll();
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ApiQuery
 */
@Retention(RUNTIME)
public @interface DefaultOrderBy {

	/**
	 * The JPQL or SQL expression to sort by (e.g., {@code "p.name"} or {@code "price"}).
	 *
	 * @return the sort expression; must not be blank
	 */
	public String value();

	/**
	 * The sort direction. Defaults to {@link com.bld.commons.reflection.type.OrderType#asc}.
	 *
	 * @return the order direction
	 */
	public OrderType orderType() default OrderType.asc;

}
