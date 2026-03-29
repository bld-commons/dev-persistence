package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.proxy.api.find.BeforeFind;

/**
 * Registers a {@link BeforeFind} hook to be invoked <em>before</em> the query
 * is executed by the {@code proxy-api-controller} interceptor.
 *
 * <p>Place this annotation on a method of an {@link ApiFindController} interface.
 * The framework instantiates (or retrieves from the Spring context) the specified
 * {@link BeforeFind} implementation and calls it with the current query parameter
 * before delegating to {@link com.bld.commons.service.JpaService}.</p>
 *
 * <p>Typical use cases: input enrichment, security checks, audit logging.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * @ApiFindController
 * public interface OrderController {
 *
 *     @PostMapping("/orders/search")
 *     @ApiBeforeFind(TenantInjector.class)   // injects tenant context before querying
 *     List<OrderDto> search(@RequestBody OrderFilter filter);
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see BeforeFind
 * @see ApiAfterFind
 * @see ApiFindController
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiBeforeFind {

	/**
	 * The {@link BeforeFind} implementation to invoke before the query.
	 * The class must be a valid Spring bean or have a no-arg constructor.
	 *
	 * @return the before-find hook class; must not be {@code null}
	 */
	public Class<? extends BeforeFind<?, ?>> value();

}
