/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.workspace.mapper.MapperModel.java
 */
package bld.commons.controller.mapper;

import bld.commons.reflection.model.BaseModel;

/**
 * The Interface ModelMapper.
 *
 * @param <E> the element type
 * @param <M> the generic type
 */
public interface ModelMapper<E,M extends BaseModel<?>> {


	/**
	 * Convert to model.
	 *
	 * @param entity the entity
	 * @return the m
	 */
	public M convertToModel(E entity);
}
