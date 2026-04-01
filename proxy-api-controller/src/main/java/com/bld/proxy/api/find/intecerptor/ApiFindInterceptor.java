package com.bld.proxy.api.find.intecerptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * JDK {@link InvocationHandler} that serves as the entry point for every
 * proxied controller method call in the {@code proxy-api-controller} framework.
 *
 * <p>This component is set as the {@link java.lang.reflect.InvocationHandler} for
 * every dynamic proxy registered by {@link com.bld.proxy.api.find.config.ApiFindRegistrar}.
 * On each method invocation it retrieves a prototype-scoped {@link FindInterceptor}
 * bean from the Spring context (ensuring thread-safety and request isolation) and
 * delegates the actual query execution to it.</p>
 *
 * <p>The {@code hashCode} method is short-circuited and returns {@code 0} to avoid
 * infinite proxy recursion when the bean is stored in hash-based collections.</p>
 *
 * @author Francesco Baldi
 * @see FindInterceptor
 * @see com.bld.proxy.api.find.config.ProxyConfig
 */
@Component
public class ApiFindInterceptor implements InvocationHandler {


	private final static Logger logger = LoggerFactory.getLogger(ApiFindInterceptor.class);

	@Autowired
	private ApplicationContext applicationContext;

	/**
	 * Intercepts every method call on a proxied {@code @ApiFindController} interface.
	 *
	 * <p>Retrieves a fresh prototype {@link FindInterceptor} bean for each invocation
	 * and delegates to {@link FindInterceptor#find(Object, Method, Object[])}.</p>
	 *
	 * @param obj    the proxy instance
	 * @param method the interface method that was invoked
	 * @param args   the runtime arguments supplied to the method call
	 * @return the query result produced by the {@link FindInterceptor}
	 * @throws Throwable if the underlying query or mapping fails
	 */
	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		if ("hashCode".equalsIgnoreCase(method.getName()))
			return 0;
		logger.info("[ApiFindInterceptor] Invoking proxy method: {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
		FindInterceptor findInterceptor = this.applicationContext.getBean(FindInterceptor.class);
		return findInterceptor.find(obj, method, args);
	}

}
