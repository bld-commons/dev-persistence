/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.workspace.WorkModel.java
 */
package bld.commons.workspace;

import bld.commons.reflection.model.BaseModel;
import bld.commons.reflection.model.CollectionResponse;
import bld.commons.reflection.model.ObjectResponse;
import bld.commons.reflection.model.QueryFilter;

/**
 * The Interface WorkModel.
 */
public interface WorkModel {

	/**
	 * Find by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param <M> the generic type
	 * @param queryFilter the query filter
	 * @return the collection response
	 * @throws Exception the exception
	 */
	public <T,ID,M extends BaseModel<ID>> CollectionResponse<M> findByFilter(QueryFilter<T,ID> queryFilter) throws Exception;

	/**
	 * Find single result by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param <M> the generic type
	 * @param queryFilter the query filter
	 * @return the object response
	 * @throws Exception the exception
	 */
	public <T, ID, M extends BaseModel<ID>> ObjectResponse<M> findSingleResultByFilter(QueryFilter<T, ID> queryFilter) throws Exception;

	/**
	 * Count by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param queryFilter the query filter
	 * @return the object response
	 */
	public <T, ID> ObjectResponse<Long> countByFilter(QueryFilter<T, ID> queryFilter);
	
}
