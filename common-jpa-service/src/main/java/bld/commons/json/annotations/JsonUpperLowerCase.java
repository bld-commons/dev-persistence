/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.json.annotations.JsonUpperLowerCase.java
 */
package bld.commons.json.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import bld.commons.reflection.annotations.deserialize.UpperLowerDeserializer;
import bld.commons.reflection.type.UpperLowerType;

/**
 * The Interface JsonUpperLowerCase.
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD,PARAMETER })
@JacksonAnnotationsInside
@JsonDeserialize(using = UpperLowerDeserializer.class)
@JsonInclude(Include.NON_NULL)
public @interface JsonUpperLowerCase {

	/**
	 * Value.
	 *
	 * @return the upper lower type
	 */
	public UpperLowerType value();
}
