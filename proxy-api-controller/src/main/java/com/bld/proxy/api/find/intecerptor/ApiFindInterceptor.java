package com.bld.proxy.api.find.intecerptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bld.proxy.api.find.data.ApiMethod;

public class ApiFindInterceptor implements InvocationHandler {

	private final static Logger logger = LoggerFactory.getLogger(ApiFindInterceptor.class);

	private final FindInterceptor findInterceptor;


	public ApiFindInterceptor(FindInterceptor findInterceptor) {
		super();
		this.findInterceptor = findInterceptor;
	}



	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws Throwable {
		if ("hashCode".equalsIgnoreCase(method.getName()))
			return 0;
		logger.info("invoke");
		return findInterceptor.find(obj, new ApiMethod(method, args));
	}

}
