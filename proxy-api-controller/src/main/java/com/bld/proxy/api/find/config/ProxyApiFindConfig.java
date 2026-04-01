package com.bld.proxy.api.find.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.bld.commons.utils.config.annotation.EnableCommonUtils;

/**
 * Spring configuration class for the {@code proxy-api-controller} module.
 *
 * <p>Activates the component scan over the {@code com.bld.proxy.api.find} package
 * so that all interceptor, registrar, and support beans are picked up automatically,
 * and enables the common utilities required by the dynamic proxy infrastructure.</p>
 *
 * <p>This class is imported automatically when the application is annotated with
 * {@link com.bld.proxy.api.find.config.annotation.EnableProxyApiController}.</p>
 *
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.config.annotation.EnableProxyApiController
 */
@Configuration
@ComponentScan(basePackages = "com.bld.proxy.api.find")
@EnableCommonUtils
public class ProxyApiFindConfig {



}
