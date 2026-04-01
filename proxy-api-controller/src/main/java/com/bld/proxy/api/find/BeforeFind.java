package com.bld.proxy.api.find;

import com.bld.commons.reflection.model.BaseQueryParameter;

/**
 * Hook interface invoked <em>before</em> the query is executed by the
 * {@code proxy-api-controller} interceptor.
 *
 * <p>Implement this interface and annotate the target controller method with
 * {@link com.bld.proxy.api.find.annotations.ApiBeforeFind} to intercept and
 * enrich the {@link BaseQueryParameter} before it is sent to the
 * {@link com.bld.commons.service.JpaService}. Typical use cases include
 * injecting tenant context, adding security constraints, or pre-populating
 * filter values.</p>
 *
 * <p>Implementations must be Spring-managed beans (e.g., annotated with
 * {@code @Component}) so that the framework can retrieve them from the
 * application context at runtime.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * @Component
 * public class TenantInjector implements BeforeFind<Order, Long> {
 *
 *     @Override
 *     public void before(BaseQueryParameter<Order, Long> parameters, Object... args) {
 *         parameters.addParameter("tenantId", TenantContext.current());
 *     }
 * }
 * }</pre>
 *
 * @param <E>  the JPA entity type
 * @param <ID> the primary-key type of the entity
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.annotations.ApiBeforeFind
 * @see AfterFind
 */
public interface BeforeFind<E,ID> {

	/**
	 * Called before the query is executed, allowing callers to enrich or
	 * modify the query parameters.
	 *
	 * @param parameters the query parameter object that will be passed to the service
	 * @param args       the original method arguments from the controller invocation
	 */
	public void before(BaseQueryParameter<E,ID> parameters,  Object... args);

}
