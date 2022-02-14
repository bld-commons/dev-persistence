/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.controller.SearchController.java
 */
package bld.commons.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.ObjectResponse;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.workspace.WorkModel;

/**
 * The Class SearchController.
 *
 * @param <ID> the generic type
 * @param <T> the generic type
 * @param <F> the generic type
 */
public class SearchController<ID,T extends BaseModel<ID>, F extends FilterParameter> {

	
	/** The work model. */
	@Autowired
	private WorkModel workModel;
	
	
	/**
	 * Find by filter.
	 *
	 * @param filter the filter
	 * @return the collection response
	 * @throws Exception the exception
	 */
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	public CollectionResponse<T> findByFilter(@RequestBody F filter) throws Exception {
		QueryFilter<?, ID> queryFilter = new QueryFilter<>(filter);
		return this.workModel.findByFilter(queryFilter);
	}
	
	
	@PostMapping(path = "/count", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	public ObjectResponse<Long> countByFilter(@RequestBody F filter) throws Exception {
		QueryFilter<?, ID> queryFilter = new QueryFilter<>(filter);
		return this.workModel.countByFilter(queryFilter);
	}
	
	
	/**
	 * Single result find by filter.
	 *
	 * @param filter the filter
	 * @return the object response
	 * @throws Exception the exception
	 */
	@PostMapping(path="/search/single-result", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	public ObjectResponse<T> singleResultFindByFilter(@RequestBody F filter) throws Exception{
		QueryFilter<?, ID>query=new QueryFilter<>(filter);
		return this.workModel.findSingleResultByFilter(query);
	}
	
	
}
