package com.bld.commons.controller.mapper;

import java.util.Map;

/**
 * Strategy interface for converting a raw {@link Map} row (from a native SQL query)
 * into a typed result object.
 *
 * <p>Implementations are referenced via the {@link com.bld.commons.reflection.annotations.ResultMapping}
 * annotation on a model field when a native query returns a column whose value
 * cannot be mapped automatically and requires custom conversion logic.</p>
 *
 * <p><b>Example</b></p>
 * <pre>{@code
 * public class StatusMapper implements ResultMapper<StatusEnum> {
 *
 *     @Override
 *     public StatusEnum mapToData(Map<String, Object> map) {
 *         return StatusEnum.fromCode((String) map.get("status_code"));
 *     }
 * }
 * }</pre>
 *
 * @param <T> the target type that the row map is converted into
 * @author Francesco Baldi
 * @see com.bld.commons.reflection.annotations.ResultMapping
 */
public interface ResultMapper<T> {

	/**
	 * Converts a raw column-value map (representing a single result-set row)
	 * into the target type {@code T}.
	 *
	 * @param map a {@link Map} where keys are column names and values are the
	 *            corresponding column values; never {@code null}
	 * @return the converted object; may be {@code null} if the row represents an absent value
	 */
	public T mapToData(Map<String,Object> map);
}
