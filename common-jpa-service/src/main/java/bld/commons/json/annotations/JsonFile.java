package bld.commons.json.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import bld.commons.reflection.annotations.deserialize.CustomByteDeserializer;
import bld.commons.reflection.annotations.serialize.CustomByteSerializer;

@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@JacksonAnnotationsInside
@JsonDeserialize(using = CustomByteDeserializer.class)
@JsonSerialize(using = CustomByteSerializer.class)
@JsonInclude(Include.NON_NULL)
public @interface JsonFile {

}
