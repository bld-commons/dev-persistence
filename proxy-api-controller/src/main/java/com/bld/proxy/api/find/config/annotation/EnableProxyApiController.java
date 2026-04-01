package com.bld.proxy.api.find.config.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.bld.proxy.api.find.config.ApiFindRegistrar;
import com.bld.proxy.api.find.config.ProxyApiFindConfig;


/**
 * Enables the {@code proxy-api-controller} framework in a Spring Boot application.
 *
 * <p>Add this annotation to any {@code @Configuration} class (typically the
 * application's main class) to activate the dynamic REST controller infrastructure.
 * It imports {@link ProxyApiFindConfig} (component scan + utilities) and
 * {@link ApiFindRegistrar} (bean registration for interfaces annotated with
 * {@link com.bld.proxy.api.find.annotations.ApiFindController}).</p>
 *
 * <p><b>Usage</b></p>
 * <pre>{@code
 * @SpringBootApplication
 * @EnableProxyApiController(basePackages = "com.example.controller")
 * public class MyApplication {
 *     public static void main(String[] args) {
 *         SpringApplication.run(MyApplication.class, args);
 *     }
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.annotations.ApiFindController
 * @see ApiFindRegistrar
 */
@Configuration
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ProxyApiFindConfig.class,ApiFindRegistrar.class})
public @interface EnableProxyApiController {

	/**
	 * Alias for {@link #basePackages()}.
	 *
	 * @return base packages to scan for {@code @ApiFindController} interfaces
	 */
	public String[] value() default {};

	/**
	 * Base packages to scan for interfaces annotated with
	 * {@link com.bld.proxy.api.find.annotations.ApiFindController}.
	 *
	 * @return the packages to scan; defaults to the package of the annotated class
	 */
	public String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()}: the package of each
	 * specified class is used as a scan root.
	 *
	 * @return marker classes whose packages should be scanned
	 */
	public Class<?>[] basePackageClasses() default {};

	/**
	 * Additional configuration classes to import into the application context.
	 *
	 * @return extra configuration classes; empty by default
	 */
	public Class<?>[] defaultConfiguration() default {};

	/**
	 * Specific controller interface classes to register directly, bypassing
	 * the classpath scan.
	 *
	 * @return controller interface classes to register; empty by default
	 */
	public Class<?>[] clients() default {};
}
