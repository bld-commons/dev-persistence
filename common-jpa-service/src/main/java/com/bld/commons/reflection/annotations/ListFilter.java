/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.ListFilter.java
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface ListFilter.
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface ListFilter {

}
