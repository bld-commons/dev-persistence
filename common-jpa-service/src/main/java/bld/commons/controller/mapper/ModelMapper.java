/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.workspace.mapper.MapperModel.java
 */
package bld.commons.controller.mapper;


public interface ModelMapper<E,M> {


	/**
	 * Convert to model.
	 *
	 * @param entity the entity
	 * @return the m
	 */
	public M convertToModel(E entity);
}
