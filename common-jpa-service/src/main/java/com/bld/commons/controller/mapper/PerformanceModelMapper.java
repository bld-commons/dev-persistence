package com.bld.commons.controller.mapper;

import com.bld.commons.utils.data.BaseModel;

public interface PerformanceModelMapper<E,M extends BaseModel<?>,PM extends BaseModel<?>> extends ModelMapper<E, M>{
	
	public PM convertToPerformanceModel(E entity);

}
