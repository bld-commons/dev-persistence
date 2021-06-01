package bld.commons.workspace;

import bld.commons.reflection.model.BasicModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.QueryFilter;

public interface WorkModel {

	public <T,ID,M extends BasicModel<ID>> CollectionResponse<M> findByFilter(QueryFilter<T,ID> queryFilter) throws Exception;
	
}
