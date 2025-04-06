package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.proxy.api.find.BeforeFind;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiBeforeFind {

	public Class<? extends BeforeFind<?,?>> value();
	

}
