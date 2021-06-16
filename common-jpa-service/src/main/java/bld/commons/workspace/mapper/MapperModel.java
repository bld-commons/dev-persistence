package bld.commons.workspace.mapper;

import bld.commons.reflection.model.BaseModel;

public interface MapperModel<T,M extends BaseModel<?>> {

	public M convertToModel(T entity);
}
