/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.ListFilter.java
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a {@link java.util.Collection} (or array) field in a
 * {@link com.bld.commons.reflection.model.BaseParameter} subclass as an SQL
 * {@code IN (…)} filter.
 *
 * <p>When the reflection engine encounters a field annotated with {@code @ListFilter},
 * it generates a JPQL condition of the form {@code AND e.field IN (:field)},
 * binding the collection directly as the named parameter value.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class OrderFilter extends BaseParameter {
 *
 *     // generates: AND o.status IN (:statuses)
 *     @ListFilter
 *     private List<String> statuses;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface ListFilter {

}
