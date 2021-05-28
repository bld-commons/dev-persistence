package bld.commons.workspace.mapper;

import bld.commons.reflection.model.BasicModel;

public interface MapperModel<T,M extends BasicModel<?>> {

	public M convertToModel(T entity);
}
