package com.bld.proxy.api.find.data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import com.bld.proxy.api.find.annotations.ApiMapper;
import com.bld.proxy.api.find.config.ApiQuery;

public class ApiMethod {

	private Method method;

	private Object[] args;
	
	private Map<Class<? extends Annotation>, ParameterDetails> map;
	
	public ApiMethod(Method method, Object[] args) {
		super();
		this.method = method;
		this.args = args;
		this.map=new HashMap<>();
	}

	private ApiQuery apiQuery;

	private ApiMapper apiMapper;

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public Object[] getArgs() {
		return args;
	}

	public void setArgs(Object... args) {
		this.args = args;
	}

	public ApiQuery getApiQuery() {
		return apiQuery;
	}

	public void setApiQuery(ApiQuery apiQuery) {
		this.apiQuery = apiQuery;
	}

	public ApiMapper getApiMapper() {
		return apiMapper;
	}

	public void setApiMapper(ApiMapper apiMapper) {
		this.apiMapper = apiMapper;
	}

	public Map<Class<? extends Annotation>, ParameterDetails> getMap() {
		return map;
	}

	public void setMap(Map<Class<? extends Annotation>, ParameterDetails> map) {
		this.map = map;
	}
	
	
	
}
