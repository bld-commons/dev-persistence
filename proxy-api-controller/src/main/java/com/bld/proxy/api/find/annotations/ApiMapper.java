package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Specifies the MapStruct (or any bean) mapper class and optional method name
 * to use for converting query results before returning them from a dynamic
 * {@link ApiFindController} method.
 *
 * <p>When the interceptor has retrieved the list of entities from the
 * {@link com.bld.commons.service.JpaService}, it looks for this annotation to
 * determine whether to apply a transformation. If {@link #method()} is blank,
 * the framework uses the first compatible single-argument method found on the
 * mapper class.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * @ApiFindController
 * public interface ProductController {
 *
 *     @GetMapping("/products")
 *     @ApiFind(entity = Product.class, id = Long.class)
 *     @ApiMapper(value = ProductMapper.class, method = "toDto")
 *     List<ProductDto> search(@RequestBody ProductFilter filter);
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ApiFindController
 * @see ApiFind
 */
@Retention(RUNTIME)
@Target({TYPE, METHOD})
public @interface ApiMapper {

	/**
	 * The mapper class whose method will be called on each result entity.
	 * The class must be a Spring-managed bean (e.g., a MapStruct mapper annotated
	 * with {@code @Mapper(componentModel = "spring")}).
	 *
	 * @return the mapper class; must not be {@code null}
	 */
	public Class<?> value();

	/**
	 * The name of the mapping method to invoke on the mapper.
	 * If left blank (default), the framework resolves the method automatically
	 * by matching the entity type to the method's parameter type.
	 *
	 * @return the mapping method name, or {@code ""} for auto-resolution
	 */
	public String method() default "";

}
