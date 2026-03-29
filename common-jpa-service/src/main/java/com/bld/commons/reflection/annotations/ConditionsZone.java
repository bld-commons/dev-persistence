/*
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.ConditionsZone.java 
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Meta-annotation used to group native SQL conditions into named zones.
 *
 * <p>{@code @ConditionsZone} is placed on custom annotation types that in turn
 * annotate fields of a {@link com.bld.commons.reflection.model.BaseParameter}
 * subclass. At runtime the framework collects all fields belonging to the same
 * zone (identified by {@link #key()}) and injects their conditions as a single
 * SQL fragment into the corresponding placeholder in the native SQL template.</p>
 *
 * <p>This mechanism allows the same SQL template to have multiple independently
 * activated WHERE blocks, each controlled by a separate group of filter fields.</p>
 *
 * <h3>Example</h3>
 * <pre>{@code
 * // Define a custom zone annotation
 * @ConditionsZone(key = "dateRange", initWhere = true)
 * @Retention(RUNTIME)
 * @Target(FIELD)
 * public @interface DateRangeZone { }
 *
 * // Use it in a filter class
 * public class ReportFilter extends BaseParameter {
 *
 *     @DateRangeZone
 *     private Date from;
 *
 *     @DateRangeZone
 *     private Date to;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see com.bld.commons.reflection.annotations.ConditionsZones
 */
@Retention(RUNTIME)
@Target({ElementType.ANNOTATION_TYPE})
public @interface ConditionsZone {

	/**
	 * The identifier of this condition zone. Must match the placeholder name
	 * used in the native SQL template (e.g., {@code ${dateRange}}).
	 *
	 * @return the zone key; must not be blank
	 */
	public String key();

	/**
	 * When {@code true} (default), the framework prepends an initial
	 * {@code WHERE} or {@code AND} keyword before the first condition in this zone.
	 * Set to {@code false} if the SQL template already contains the keyword.
	 *
	 * @return whether to auto-initialise the WHERE clause for this zone
	 */
	public boolean initWhere() default true;

}
