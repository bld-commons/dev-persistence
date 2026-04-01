package com.bld.commons.reflection.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.bld.commons.controller.mapper.ResultMapper;

/**
 * Associates a field of a native-query result model with a custom
 * {@link ResultMapper} that converts the raw column value into the field type.
 *
 * <p>When the reflection engine cannot map a result-set column to a field
 * automatically (e.g., the column is a JSON blob or a composite value), annotate
 * the field with {@code @ResultMapping} and supply a {@link ResultMapper}
 * implementation that performs the conversion.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class OrderSummary {
 *
 *     @ResultMapping(StatusMapper.class)
 *     private StatusEnum status;
 * }
 * }</pre>
 *
 * @author Francesco Baldi
 * @see ResultMapper
 * @see IgnoreResultSet
 */
@Retention(RUNTIME)
@Target({FIELD})
public @interface ResultMapping {

	/**
	 * The {@link ResultMapper} implementation to use for converting the raw
	 * column value into this field's type.
	 *
	 * @return the mapper class; must not be {@code null}
	 */
	public Class<? extends ResultMapper<?>> value();
}
