package com.bld.proxy.api.find.config;

import java.lang.reflect.Proxy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.bld.proxy.api.find.intecerptor.ApiFindInterceptor;

@Component
public class ProxyConfig {

	@Autowired
	private ApiFindInterceptor apiFindInterceptor;
	
	@SuppressWarnings("unchecked")
	public <T> T newProxyInstance(Class<T> classT) {
		T t=(T) Proxy.newProxyInstance(classT.getClassLoader(), new Class<?>[] { classT }, this.apiFindInterceptor);
		return t;
	}
	
}
