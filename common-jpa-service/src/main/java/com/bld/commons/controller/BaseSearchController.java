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

public abstract class BaseSearchController<E, ID, M extends BaseModel<ID>, P extends BaseParameter, MM extends ModelMapper<E, M>> {

	/** The jpa service. */
	@Autowired
	protected JpaService<E, ID> jpaService;
	
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
	
	
	
	


	protected ObjectResponse<Long> countByFilter(@RequestBody P baseParameter) throws Exception {
		QueryParameter<E, ID> queryFilter = new QueryParameter<>(baseParameter);
		Long count = this.jpaService.countByFilter(queryFilter);
		return new ObjectResponse<>(count);
	}
	
	


	protected ObjectResponse<M> singleResultFindByFilter(@RequestBody P baseParameter) throws Exception{
		QueryParameter<E, ID>query=new QueryParameter<>(baseParameter);
		ObjectResponse<M> response = new ObjectResponse<>();
		E entity = this.jpaService.singleResultByFilter(query);
		response.setData(this.modelMapper().convertToModel(entity));
		return response;
	}

	protected abstract MM modelMapper();

	
	
}
