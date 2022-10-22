/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.json.annotations.JsonDateTimeZone.java
 */
package bld.commons.json.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bld.commons.reflection.annotations.deserialize.DateDeserializer;
import bld.commons.reflection.annotations.serialize.DateSerializer;

/**
 * The Interface JsonDateTimeZone.
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD,PARAMETER })
@JacksonAnnotationsInside
@JsonDeserialize(using = DateDeserializer.class)
@JsonSerialize(using=DateSerializer.class)
//@JsonInclude(Include.NON_NULL)
public @interface JsonDateTimeZone {

	/**
	 * Time zone.
	 *
	 * @return the string
	 */
	public String timeZone() default "${spring.jackson.time-zone}";
	
	
	/**
	 * Format.
	 *
	 * @return the string
	 */
	public String format() default "yyyy-MM-dd'T'HH:mm:ss";
	
}
