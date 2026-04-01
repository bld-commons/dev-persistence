package com.bld.commons.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.bld.commons.controller.mapper.PerformanceModelMapper;
import com.bld.commons.reflection.model.BaseParameter;
import com.bld.commons.reflection.model.QueryParameter;
import com.bld.commons.utils.data.BaseModel;
import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;

import jakarta.validation.Valid;

/**
 * Abstract controller that extends {@link BaseSearchController} to expose an
 * additional high-performance search endpoint alongside the standard ones.
 *
 * <p>Subclasses can expose two distinct collection endpoints for the same entity:</p>
 * <ul>
 *   <li>{@code POST /search} — returns the full model {@code M} (inherited from
 *       {@link BaseSearchController})</li>
 *   <li>{@code POST /performance/search} — returns the lightweight performance model
 *       {@code PM} mapped by {@link PerformanceModelMapper#convertToPerformanceModel(Object)}</li>
 * </ul>
 *
 * <p>The performance variant is useful when the response payload needs to be
 * smaller (e.g., for list views that do not require all entity fields).</p>
 *
 * @param <E>  the JPA entity type
 * @param <ID> the primary-key type of the entity
 * @param <M>  the full DTO / model type
 * @param <PM> the lightweight performance model type
 * @param <P>  the filter / parameter type accepted by endpoints
 * @author Francesco Baldi
 * @see BaseSearchController
 * @see PerformanceModelMapper
 */
public abstract class PerformanceSearchController<E,ID,M extends BaseModel<ID>,PM extends BaseModel<ID>, P extends BaseParameter> extends BaseSearchController<E, ID, M, P,PerformanceModelMapper<E, M,PM>>{


	@Autowired
	private PerformanceModelMapper<E, M,PM> modelMapper;


	/**
	 * Executes a paginated find query and maps the results to the lightweight
	 * performance model type {@code PM}.
	 *
	 * @param baseParameter the filter and pagination parameters
	 * @return a {@link CollectionResponse} containing the performance models and the total count
	 * @throws Exception if the query or mapping fails
	 */
	@PostMapping(path = "/performance/search", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	@Valid
	@Transactional
	public CollectionResponse<PM> speedUpFindByFilter(@RequestBody P baseParameter) throws Exception {
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		CollectionResponse<PM> response = new CollectionResponse<>();
		List<E> list = this.jpaService.findByFilter(queryFilter);
		Long totalCount = this.jpaService.countByFilter(queryFilter);
		List<PM> listModel = new ArrayList<>();
		for(E entity:list) {
			PM model=this.modelMapper().convertToPerformanceModel(entity);
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

	@Override
	protected PerformanceModelMapper<E, M, PM> modelMapper() {
		return this.modelMapper;
	}

	/**
	 * Executes a paginated find query and maps the results to the full model type {@code M}.
	 *
	 * @param baseParameter the filter and pagination parameters
	 * @return a {@link CollectionResponse} containing the mapped models and the total count
	 * @throws Exception if the query or mapping fails
	 */
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
