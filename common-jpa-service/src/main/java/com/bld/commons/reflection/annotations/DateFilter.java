/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.DateFilter.java
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;


/**
 * Marks a {@link java.util.Date} (or {@link java.util.Calendar}) field in a
 * {@link com.bld.commons.reflection.model.BaseParameter} subclass as a date filter
 * with an optional temporal offset.
 *
 * <p>When the reflection engine processes the filter object, it shifts the field
 * value by the specified amount before binding it as a JPQL parameter.
 * Positive values move the date forward; negative values move it backward.</p>
 *
 * <p><b>Example – find records expiring within the next 7 days</b></p>
 * <pre>{@code
 * public class ContractFilter extends BaseParameter {
 *
 *     // binds as (now + 7 days) – matches records expiring before that date
 *     @DateFilter(addDay = 7)
 *     private Date expiresBy = new Date();
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface DateFilter {

	/**
	 * Number of years to add to the date value before binding it as a parameter.
	 * Use negative values to subtract.
	 *
	 * @return the year offset (default 0)
	 */
	public int addYear() default 0;

	/**
	 * Number of months to add to the date value before binding it as a parameter.
	 *
	 * @return the month offset (default 0)
	 */
	public int addMonth() default 0;

	/**
	 * Number of weeks to add to the date value before binding it as a parameter.
	 *
	 * @return the week offset (default 0)
	 */
	public int addWeek() default 0;

	/**
	 * Number of days to add to the date value before binding it as a parameter.
	 *
	 * @return the day offset (default 0)
	 */
	public int addDay() default 0;

	/**
	 * Number of hours to add to the date value before binding it as a parameter.
	 *
	 * @return the hour offset (default 0)
	 */
	public int addHour() default 0;

	/**
	 * Number of minutes to add to the date value before binding it as a parameter.
	 *
	 * @return the minute offset (default 0)
	 */
	public int addMinute() default 0;

	/**
	 * Number of seconds to add to the date value before binding it as a parameter.
	 *
	 * @return the second offset (default 0)
	 */
	public int addSecond() default 0;
	
	

	
	

}
