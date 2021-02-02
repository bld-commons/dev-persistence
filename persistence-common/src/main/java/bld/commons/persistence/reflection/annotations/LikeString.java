package bld.commons.persistence.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.persistence.reflection.type.LikeType;

@Retention(RUNTIME)
@Target(FIELD)
public @interface LikeString {
	
	public LikeType likeType() default LikeType.LEFT_RIGHT; 
	
	public boolean ignoreCase() default true;
	
}
