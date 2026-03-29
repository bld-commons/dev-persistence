package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Binds a controller interface or method to a specific JPA entity and its
 * primary-key type, enabling the {@code proxy-api-controller} framework to
 * resolve and execute the correct {@link com.bld.commons.service.JpaService}
 * at runtime.
 *
 * <p>When placed on a type (interface) annotated with {@link ApiFindController},
 * all methods of that interface inherit the entity binding unless they declare
 * their own {@code @ApiFind} at method level.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * @ApiFindController
 * @ApiFind(entity = Product.class, id = Long.class)
 * public interface ProductController {
 *
 *     @GetMapping("/products")
 *     List<ProductDto> search(@RequestBody ProductFilter filter);
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ApiFindController
 * @see ApiMapper
 */
@Retention(RUNTIME)
@Target({METHOD, TYPE})
public @interface ApiFind {

	/**
	 * The JPA entity class that this controller method queries.
	 *
	 * @return the entity class; must not be {@code null}
	 */
	public Class<?> entity();

	/**
	 * The type of the entity's primary key (e.g., {@code Long.class}, {@code UUID.class}).
	 *
	 * @return the primary-key class; must not be {@code null}
	 */
	public Class<?> id();
}
