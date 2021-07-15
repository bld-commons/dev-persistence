package bld.commons.workspace;

import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.ObjectResponse;
import bld.commons.reflection.model.QueryFilter;

public interface WorkModel {

	public <T,ID,M extends BaseModel<ID>> CollectionResponse<M> findByFilter(QueryFilter<T,ID> queryFilter) throws Exception;

	public <T, ID, M extends BaseModel<ID>> ObjectResponse<M> findSingleResultByFilter(QueryFilter<T, ID> queryFilter) throws Exception;
	
}
