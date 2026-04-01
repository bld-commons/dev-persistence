package com.bld.commons.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;

import com.bld.commons.controller.mapper.ModelMapper;
import com.bld.commons.reflection.model.BaseParameter;
import com.bld.commons.reflection.model.QueryParameter;
import com.bld.commons.service.JpaService;
import com.bld.commons.utils.data.BaseModel;
import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;

/**
 * Abstract base controller that wires a {@link JpaService} to standard search
 * endpoints exposed as Spring MVC handler methods.
 *
 * <p>Subclasses inherit the core query execution logic (find, count, single-result)
 * and only need to implement {@link #modelMapper()} to supply the correct
 * {@link ModelMapper} instance for the target entity type.</p>
 *
 * <p>This class does not expose any HTTP endpoints directly; endpoint mappings are
 * declared in concrete subclasses (e.g., {@link PerformanceSearchController}).</p>
 *
 * @param <E>  the JPA entity type
 * @param <ID> the primary-key type of the entity
 * @param <M>  the DTO / model type returned to callers
 * @param <P>  the filter / parameter type accepted by endpoints
 * @param <MM> the {@link ModelMapper} implementation used to convert entities to models
 * @author Francesco Baldi
 * @see PerformanceSearchController
 * @see ModelMapper
 */
public abstract class BaseSearchController<E, ID, M extends BaseModel<ID>, P extends BaseParameter, MM extends ModelMapper<E, M>> {

	/** The jpa service. */
	@Autowired
	protected JpaService<E, ID> jpaService;

	/**
	 * Executes a paginated find query and maps the results to the model type.
	 *
	 * @param baseParameter the filter and pagination parameters
	 * @return a {@link CollectionResponse} containing the mapped models and the total count
	 * @throws Exception if the query or mapping fails
	 */
	protected CollectionResponse<M> findByFilter(P baseParameter) throws Exception {
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		CollectionResponse<M> response = new CollectionResponse<>();
		List<E> list = this.jpaService.findByFilter(queryFilter);
		Long totalCount = this.jpaService.countByFilter(queryFilter);
		List<M> listModel = new ArrayList<>();
		for(E entity:list) {
			M model=this.modelMapper().convertToModel(entity);
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
	 * Returns the total number of records that match the given filter.
	 *
	 * @param baseParameter the filter parameters
	 * @return an {@link ObjectResponse} wrapping the count value
	 * @throws Exception if the query fails
	 */
	protected ObjectResponse<Long> countByFilter(@RequestBody P baseParameter) throws Exception {
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		Long count = this.jpaService.countByFilter(queryFilter);
		return new ObjectResponse<>(count);
	}
	
	


	/**
	 * Returns a single entity that matches the given filter, mapped to the model type.
	 *
	 * @param baseParameter the filter parameters; must identify a unique record
	 * @return an {@link ObjectResponse} wrapping the matched model
	 * @throws Exception if the query fails or returns more than one result
	 */
	protected ObjectResponse<M> singleResultFindByFilter(@RequestBody P baseParameter) throws Exception{
		QueryParameter<E, ID>query=new QueryParameter<>(baseParameter);
		ObjectResponse<M> response = new ObjectResponse<>();
		E entity = this.jpaService.singleResultByFilter(query);
		response.setData(this.modelMapper().convertToModel(entity));
		return response;
	}

	/**
	 * Returns the {@link ModelMapper} instance used to convert entities to models.
	 *
	 * @return the model mapper; must not be {@code null}
	 */
	protected abstract MM modelMapper();

	
	
}
