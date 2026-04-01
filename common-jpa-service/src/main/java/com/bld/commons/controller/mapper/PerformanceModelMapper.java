package com.bld.commons.controller.mapper;

import com.bld.commons.utils.data.BaseModel;

/**
 * Extension of {@link ModelMapper} that adds a lightweight conversion path for
 * high-performance use cases.
 *
 * <p>Implement this interface when the same entity {@code E} needs to be mapped
 * to two different representations:</p>
 * <ul>
 *   <li>{@code M} — the full model returned by standard {@code /search} endpoints
 *       (inherited from {@link ModelMapper#convertToModel(Object)})</li>
 *   <li>{@code PM} — a reduced performance model returned by the
 *       {@code /performance/search} endpoint, containing only the fields required
 *       for list views or summary pages</li>
 * </ul>
 *
 * @param <E>  the JPA entity type
 * @param <M>  the full DTO / model type
 * @param <PM> the lightweight performance model type
 * @author Francesco Baldi
 * @see ModelMapper
 * @see com.bld.commons.controller.PerformanceSearchController
 */
public interface PerformanceModelMapper<E,M extends BaseModel<?>,PM extends BaseModel<?>> extends ModelMapper<E, M>{

	/**
	 * Converts a JPA entity to its lightweight performance model representation.
	 *
	 * @param entity the source entity; must not be {@code null}
	 * @return the performance model; never {@code null}
	 */
	public PM convertToPerformanceModel(E entity);

}
