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

import bld.commons.reflection.annotations.deserialize.CustomDateDeserializer;

// TODO: Auto-generated Javadoc
/**
 * The Interface JsonDateFilter.
 */
@Retention(RUNTIME)
@Target({ FIELD, METHOD })
@JacksonAnnotationsInside
@JsonDeserialize(using = CustomDateDeserializer.class)
//@JsonSerialize(using=CustomDateSerializer.class)
@JsonInclude(Include.NON_NULL)
public @interface JsonDateFilter {

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
	
	
	/**
	 * Equals.
	 *
	 * @return true, if successful
	 */
	public boolean equals() default true;
	
	/**
	 * Adds the year.
	 *
	 * @return the int
	 */
	public int addYear() default 0;
	
	/**
	 * Adds the month.
	 *
	 * @return the int
	 */
	public int addMonth() default 0;
	
	/**
	 * Adds the week.
	 *
	 * @return the int
	 */
	public int addWeek() default 0;
	
	/**
	 * Adds the day.
	 *
	 * @return the int
	 */
	public int addDay() default 0;
	
	/**
	 * Adds the hour.
	 *
	 * @return the int
	 */
	public int addHour() default 0;
	
	/**
	 * Adds the minute.
	 *
	 * @return the int
	 */
	public int addMinute() default 0;
	
	/**
	 * Adds the second.
	 *
	 * @return the int
	 */
	public int addSecond() default 0;
	
}
