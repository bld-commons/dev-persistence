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

import com.bld.commons.utils.data.BaseModel;
import com.bld.commons.utils.data.CollectionResponse;
import com.bld.commons.utils.data.ObjectResponse;

import bld.commons.controller.mapper.PerformanceModelMapper;
import bld.commons.reflection.model.BaseParameter;
import bld.commons.reflection.model.QueryParameter;

public abstract class PerformanceSearchController<E,ID,M extends BaseModel<ID>,PM extends BaseModel<ID>, P extends BaseParameter> extends BaseSearchController<E, ID, M, P,PerformanceModelMapper<E, M,PM>>{

	
	@Autowired
	private PerformanceModelMapper<E, M,PM> modelMapper;
	

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
