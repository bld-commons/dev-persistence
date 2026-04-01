package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks a field of a native-query result model to be excluded from the
 * automatic result-set mapping performed by the reflection engine.
 *
 * <p>When the framework maps rows returned by a native SQL query to a model
 * class, it iterates over all fields and attempts to populate them from the
 * result-set columns. Fields annotated with {@code @IgnoreResultSet} are skipped
 * entirely, leaving them at their default (usually {@code null}) value.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class ProductSummary {
 *
 *     private String name;
 *
 *     @IgnoreResultSet          // computed at service level, not from the query
 *     private String displayLabel;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ResultMapping
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface IgnoreResultSet {

}
