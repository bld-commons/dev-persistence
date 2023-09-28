package com.bld.proxy.api.find.config;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ApiQuery {

	public String value();

	public boolean jpql() default false;

	public DefaultOrderBy[] orderBy() default {};

}
