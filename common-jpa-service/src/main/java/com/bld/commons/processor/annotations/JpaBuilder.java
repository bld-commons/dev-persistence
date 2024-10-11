/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.annotations.JpaBuilder.java
 */
package com.bld.commons.processor.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface JpaBuilder.
 */
@Retention(SOURCE)
@Target(TYPE)
public @interface JpaBuilder {

	/**
	 * Service package.
	 *
	 * @return the string
	 */
	public String servicePackage();
	
	/**
	 * Repository package.
	 *
	 * @return the string
	 */
	public String repositoryPackage();
	
}
