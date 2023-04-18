/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.controller.SearchController.java
 */
package bld.commons.controller;

import org.springframework.beans.factory.annotation.Autowired;

import com.bld.commons.utils.data.BaseModel;

import bld.commons.controller.mapper.ModelMapper;
import bld.commons.reflection.model.BaseParameter;


/**
 * The Class SearchController.
 *
 * @param <E> the element type
 * @param <ID> the generic type
 * @param <M> the generic type
 * @param <P> the generic type
 */
public abstract class SearchController<E, ID, M extends BaseModel<ID>, P extends BaseParameter> extends BaseSearchController<E, ID, M, P, ModelMapper<E, M>> {

	/** The model mapper. */
	@Autowired
	private ModelMapper<E, M> modelMapper;

	/**
	 * Model mapper.
	 *
	 * @return the model mapper
	 */
	@Override
	protected ModelMapper<E, M> modelMapper() {
		return this.modelMapper;
	}

}
