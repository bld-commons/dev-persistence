/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.DateFilter.java
 */
package bld.commons.persistence.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import bld.commons.persistence.reflection.type.DateType;

/**
 * The Interface ToCalendar.
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface DateFilter {

	/**
	 * Date type.
	 *
	 * @return the date type
	 */
	public DateType dateType() default DateType.DATE;
	
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
