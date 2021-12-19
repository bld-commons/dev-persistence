/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.workspace.mapper.MapperModel.java
 */
package bld.commons.workspace.mapper;

import bld.commons.reflection.model.BaseModel;

/**
 * The Interface MapperModel.
 *
 * @param <T> the generic type
 * @param <M> the generic type
 */
public interface MapperModel<T,M extends BaseModel<?>> {

	/**
	 * Convert to model.
	 *
	 * @param entity the entity
	 * @return the m
	 */
	public M convertToModel(T entity);
}
