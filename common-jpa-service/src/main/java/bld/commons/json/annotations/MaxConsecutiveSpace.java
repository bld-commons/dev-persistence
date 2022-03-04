package bld.commons.json.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bld.commons.reflection.annotations.deserialize.MaxConsecutiveSpaceDeserializer;

@Retention(RUNTIME)
@Target({ FIELD, METHOD, PARAMETER })
@JacksonAnnotationsInside
@JsonDeserialize(using = MaxConsecutiveSpaceDeserializer.class)
public @interface MaxConsecutiveSpace {

	public boolean removeAllSpaceType() default false;
	
	public int consecutive() default 1;
	
	public boolean trim() default true;
	
	public boolean removeEndline() default false;
	
}
