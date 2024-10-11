/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.config.annotation.EnableJpaService.java
 * 
 */
package com.bld.commons.config.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.bld.commons.config.EnableJpaServiceConfiguration;

/**
 * The Interface EnableJpaService.
 */
@Configuration
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(EnableJpaServiceConfiguration.class)
public @interface EnableJpaService {

}
