package com.bld.proxy.api.find.config;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

import com.bld.commons.reflection.type.OrderType;

@Retention(RUNTIME)
public @interface DefaultOrderBy {

	public String value();
	
	public OrderType orderType() default OrderType.asc; 
	
}
