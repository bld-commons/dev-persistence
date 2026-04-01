package com.bld.proxy.api.find.config;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bld.proxy.api.find.intecerptor.ApiFindInterceptor;

/**
 * Factory bean that creates Java dynamic proxy instances for
 * {@link com.bld.proxy.api.find.annotations.ApiFindController} interfaces.
 *
 * <p>{@code ProxyConfig} is a Spring component that wraps the JDK
 * {@link java.lang.reflect.Proxy} API. It is used by {@link ApiFindRegistrar}
 * as a factory method ({@code proxyConfig.newProxyInstance(interfaceClass)}) when
 * registering each controller interface as a bean in the application context.</p>
 *
 * <p>All proxy calls are delegated to
 * {@link com.bld.proxy.api.find.intecerptor.ApiFindInterceptor}, which handles
 * the actual query resolution and execution.</p>
 *
 * @author Francesco Baldi
 * @see com.bld.proxy.api.find.intecerptor.ApiFindInterceptor
 * @see ApiFindRegistrar
 */
@Component
public class ProxyConfig {

	@Autowired
	private ApiFindInterceptor apiFindInterceptor;

	/**
	 * Creates a new JDK dynamic proxy for the given interface, backed by
	 * {@link com.bld.proxy.api.find.intecerptor.ApiFindInterceptor}.
	 *
	 * @param <T>    the interface type
	 * @param classT the interface class to proxy; must not be {@code null}
	 * @return a proxy instance that implements {@code classT}
	 */
	@SuppressWarnings("unchecked")
	public <T> T newProxyInstance(Class<T> classT) {
		T t=(T) Proxy.newProxyInstance(classT.getClassLoader(), new Class<?>[] { classT }, this.apiFindInterceptor);
		return t;
	}

}
