package bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.controller.mapper.ResultMapper;

@Retention(RUNTIME)
@Target({FIELD})
public @interface ResultMap {

	public Class<? extends ResultMapper<?>> value();
}
