package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface ApiMapper {

	public Class<?> bean();
	
	public String method();
	
}
