/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.controller.SearchController.java
 */
package com.bld.commons.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bld.commons.controller.mapper.ModelMapper;
import com.bld.commons.reflection.model.BaseParameter;
import com.bld.commons.utils.data.BaseModel;
import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;

import jakarta.validation.Valid;


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
	
	
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	@Transactional
	public CollectionResponse<M> findByFilter(@RequestBody P baseParameter) throws Exception {
		return super.findByFilter(baseParameter);
	}
	
	/**
	 * Count by filter.
	 *
	 * @param baseParameter the base parameter
	 * @return the object response
	 * @throws Exception the exception
	 */
	@PostMapping(path = "/count", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	@Transactional
	public ObjectResponse<Long> countByFilter(@RequestBody P baseParameter) throws Exception {
		return super.countByFilter(baseParameter);
	}
	
	

	/**
	 * Single result find by filter.
	 *
	 * @param baseParameter the base parameter
	 * @return the object response
	 * @throws Exception the exception
	 */
	@PostMapping(path="/search/single-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	@Transactional
	public ObjectResponse<M> singleResultFindByFilter(@RequestBody P baseParameter) throws Exception{
		return super.singleResultFindByFilter(baseParameter);
	}

}
