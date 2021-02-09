/**************************************************************************
 * 
 * Copyright 2018 (C) DXC Technology
 * 
 * Author      : DXC Technology
 * Project Name: pmg-common
 * Package     : com.bld.pmg.annotations
 * File Name   : ExcludeFromMap.java
 *
 ***************************************************************************/
package bld.commons.persistence.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface ExcludeFromMap.
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD})
public @interface IgnoreMapping {

}
