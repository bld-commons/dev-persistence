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

	public DateType dateType() default DateType.DATE;
	
	public int addDay() default 0;
	
	public int addMonth() default 0;
	
	public int addYear() default 0;
	
}
