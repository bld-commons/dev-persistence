package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.commons.controller.mapper.ResultMapper;

@Retention(RUNTIME)
@Target({FIELD})
public @interface ResultMapping {

	public Class<? extends ResultMapper<?>> value();
}
