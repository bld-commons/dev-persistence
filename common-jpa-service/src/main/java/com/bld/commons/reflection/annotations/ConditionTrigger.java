/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.annotations.ConditionTrigger.java
 */
package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a {@link Boolean} field in a
 * {@link com.bld.commons.reflection.model.BaseParameter} subclass as a trigger
 * for a value-less JPQL condition (typically {@code IS NULL} / {@code IS NOT NULL}).
 *
 * <p>When the reflection engine encounters a field annotated with {@code @ConditionTrigger}
 * whose value is {@code true}, it registers the field name via
 * {@link com.bld.commons.reflection.model.QueryParameter#addNullable(String)},
 * activating the matching {@code @ConditionBuilder} (e.g. with
 * {@code operation = OperationType.IS_NULL} or {@code IS_NOT_NULL}) without
 * binding any parameter value.</p>
 *
 * <p>If the value is {@code false} or {@code null}, the condition is not added.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class ServiceRestFilter extends BaseParameter {
 *
 *     // when true → activates: AND e.httpMethod IS NOT NULL
 *     @ConditionTrigger
 *     private Boolean httpMethodIsNotNull;
 *
 *     // when true → activates: AND e.httpMethod IS NULL
 *     @ConditionTrigger
 *     private Boolean httpMethodIsNull;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 */
@Retention(RUNTIME)
@Target({FIELD,METHOD,PARAMETER})
public @interface ConditionTrigger {

}
