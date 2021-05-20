package bld.commons.config.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import bld.commons.config.EnableJpaServiceConfiguration;

@Configuration
@Documented
@Retention(RUNTIME)
@Target(TYPE)
@Import(EnableJpaServiceConfiguration.class)
public @interface EnableJpaService {

}
