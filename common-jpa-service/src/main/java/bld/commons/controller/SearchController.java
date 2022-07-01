/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.controller.SearchController.java
 */
package bld.commons.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import bld.commons.controller.mapper.ModelMapper;
import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.BaseParameter;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.ObjectResponse;
import bld.commons.reflection.model.QueryParameter;
import bld.commons.service.JpaService;


/**
 * The Class SearchController.
 *
 * @param <E> the element type
 * @param <ID> the generic type
 * @param <M> the generic type
 * @param <P> the generic type
 */
public abstract class SearchController<E,ID,M extends BaseModel<ID>, P extends BaseParameter> {

	
	/** The jpa service. */
	@Autowired
	private JpaService<E, ID> jpaService;
	
//	/** The model mapper. */
	@Autowired
	private ModelMapper<E,M> modelMapper;
	
	

	/**
	 * Find by filter.
	 *
	 * @param baseParameter the base parameter
	 * @return the collection response
	 * @throws Exception the exception
	 */
	@PostMapping(path = "/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	@Transactional
	public CollectionResponse<M> findByFilter(@RequestBody P baseParameter) throws Exception {
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		CollectionResponse<M> response = new CollectionResponse<>();
		List<E> list = this.jpaService.findByFilter(queryFilter);
		Long totalCount = this.jpaService.countByFilter(queryFilter);
		List<M> listModel = new ArrayList<M>();
		for(E entity:list) {
			M model=this.modelMapper.convertToModel(entity);
			listModel.add(model);
		}
		response.setData(listModel);
		response.setTotalCount(totalCount != null ? totalCount : Long.valueOf(0));
		if(queryFilter.getPageable()!=null) {
			response.setPageNumber(queryFilter.getPageable().getPageNumber());
			response.setPageSize(queryFilter.getPageable().getPageSize());
			
		}
		
		return response;
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
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		Long count = this.jpaService.countByFilter(queryFilter);
		return new ObjectResponse<>(count);
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
		QueryParameter<E, ID>query=new QueryParameter<>(baseParameter);
		ObjectResponse<M> response = new ObjectResponse<>();
		E entity = this.jpaService.findSingleResultByFilter(query);
		response.setData(this.modelMapper.convertToModel(entity));
		return response;
	}
	
	
}
