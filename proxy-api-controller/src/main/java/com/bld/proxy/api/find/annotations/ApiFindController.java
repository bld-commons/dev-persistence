package com.bld.proxy.api.find.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import org.springframework.web.bind.annotation.RestController;

@Retention(RUNTIME)
@Target(TYPE)
@RestController
public @interface ApiFindController {

}
