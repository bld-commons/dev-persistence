package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RestController;

/**
 * Marks an interface as a dynamic REST controller whose methods are intercepted
 * at runtime by the {@code proxy-api-controller} framework.
 *
 * <p>Interfaces annotated with {@code @ApiFindController} are registered as
 * Spring beans through a Java dynamic proxy ({@link java.lang.reflect.Proxy}).
 * Each method call is handled by {@link com.bld.proxy.api.find.intecerptor.ApiFindInterceptor},
 * which resolves the target {@link com.bld.commons.service.JpaService}, builds the
 * {@link com.bld.commons.reflection.model.QueryParameter}, and returns the mapped results.</p>
 *
 * <p>This annotation is meta-annotated with {@link RestController}, so Spring
 * picks up the proxied bean as a standard REST controller and maps its
 * {@code @RequestMapping} endpoints automatically.</p>
 *
 * <p><b>Typical usage</b></p>
 * <pre>{@code
 * @ApiFindController
 * @ApiFind(entity = Order.class, id = Long.class)
 * @RequestMapping("/api/orders")
 * public interface OrderController {
 *
 *     @PostMapping("/search")
 *     @ApiMapper(value = OrderMapper.class, method = "toDto")
 *     List<OrderDto> search(@RequestBody OrderFilter filter);
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ApiFind
 * @see ApiMapper
 * @see ApiBeforeFind
 * @see ApiAfterFind
 */
@Retention(RUNTIME)
@Target(TYPE)
@RestController
public @interface ApiFindController {

}
