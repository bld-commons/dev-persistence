package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.proxy.api.find.AfterFind;

/**
 * Registers an {@link AfterFind} hook to be invoked <em>after</em> the query
 * results are returned by the {@code proxy-api-controller} interceptor.
 *
 * <p>Place this annotation on a method of an {@link ApiFindController} interface.
 * The framework calls the specified {@link AfterFind} implementation with the
 * list of results before returning the response, allowing post-processing such as
 * enrichment, filtering, or side effects.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * @ApiFindController
 * public interface OrderController {
 *
 *     @PostMapping("/orders/search")
 *     @ApiAfterFind(OrderEnricher.class)   // enriches results with extra data
 *     List<OrderDto> search(@RequestBody OrderFilter filter);
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see AfterFind
 * @see ApiBeforeFind
 * @see ApiFindController
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiAfterFind {

	/**
	 * The {@link AfterFind} implementation to invoke after the query returns results.
	 * The class must be a valid Spring bean or have a no-arg constructor.
	 *
	 * @return the after-find hook class; must not be {@code null}
	 */
	public Class<? extends AfterFind<?>> value();

}
