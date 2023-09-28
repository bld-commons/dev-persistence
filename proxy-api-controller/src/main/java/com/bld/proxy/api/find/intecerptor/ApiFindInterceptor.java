package com.bld.proxy.api.find.intecerptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ApiFindInterceptor implements InvocationHandler {


	private final static Logger logger = LoggerFactory.getLogger(ApiFindInterceptor.class);

	@Autowired
	private ApplicationContext applicationContext;

	
	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		if ("hashCode".equalsIgnoreCase(method.getName()))
			return 0;
		logger.info("invoke");
		FindInterceptor findInterceptor = this.applicationContext.getBean(FindInterceptor.class);
		return findInterceptor.find(obj, method, args);
	}

}
