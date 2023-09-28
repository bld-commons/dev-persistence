/*
 * @auth Francesco Baldi
 * @class com.bld.crypto.config.annotation.EnableCrypto.java
 */
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


@Configuration
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import({ProxyApiFindConfig.class,ApiFindRegistrar.class})
public @interface EnableProxyApiController {

	public String[] value() default {};

	
	public String[] basePackages() default {};

	public Class<?>[] basePackageClasses() default {};

	public Class<?>[] defaultConfiguration() default {};

	public Class<?>[] clients() default {};
}
