package com.bld.proxy.api.find;

/**
 * Hook interface invoked <em>after</em> the query results are returned by the
 * {@code proxy-api-controller} interceptor.
 *
 * <p>Implement this interface and annotate the target controller method with
 * {@link com.bld.proxy.api.find.annotations.ApiAfterFind} to post-process the
 * result before it is returned to the caller. Typical use cases include enriching
 * DTOs with additional data, filtering sensitive fields, or triggering side effects
 * such as audit logging.</p>
 *
 * <p>Implementations must be Spring-managed beans (e.g., annotated with
 * {@code @Component}) so that the framework can retrieve them from the
 * application context at runtime.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * @Component
 * public class OrderEnricher implements AfterFind<List<OrderDto>> {
 *
 *     @Override
 *     public List<OrderDto> after(List<OrderDto> result, Object... args) {
 *         result.forEach(o -> o.setLabel(resolveLabel(o)));
 *         return result;
 *     }
 * }
 * }</pre>
 *
 * @param <T> the type of the query result (e.g., {@code List<OrderDto>} or
 *            {@link com.bld.commons.utils.data.CollectionResponse})
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.annotations.ApiAfterFind
 * @see BeforeFind
 */
public interface AfterFind<T> {

	/**
	 * Called after the query has been executed and the result has been mapped,
	 * allowing callers to transform or enrich the response.
	 *
	 * @param result the query result produced by the interceptor
	 * @param args   the original method arguments from the controller invocation
	 * @return the (possibly modified) result to return to the HTTP caller
	 */
	public T after(T result, Object... args);

}
