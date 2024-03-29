package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface ApiMapper.
 */
@Retention(RUNTIME)
@Target({ TYPE, METHOD })
public @interface ApiMapper {

	/**
	 * Value.
	 *
	 * @return the class
	 */
	public Class<?> value();
	
	/**
	 * Method.
	 *
	 * @return the string
	 */
	public String method() default "";
	
}
