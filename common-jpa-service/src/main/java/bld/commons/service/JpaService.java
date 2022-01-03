/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JpaService.java
 */
package bld.commons.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import bld.commons.reflection.model.QueryFilter;
import bld.commons.utils.PersistenceMap;

// TODO: Auto-generated Javadoc
/**
 * The Interface BaseEntityService.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
public interface JpaService<T, ID> {

	/**
	 * Count.
	 *
	 * @return the long
	 */
	public abstract long count();

	/**
	 * Delete.
	 *
	 * @param entity the entity
	 */
	public abstract void delete(T entity);

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the t
	 */
	public abstract T findById(ID id);

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	public abstract List<T> findAll();

	/**
	 * Save.
	 *
	 * @param entity the entity
	 */
	public abstract void save(T entity);

	/**
	 * Update.
	 *
	 * @param entity the entity
	 * @return the t
	 */
	public abstract T update(T entity);

	/**
	 * Save and flush.
	 *
	 * @param entity the entity
	 */
	public void saveAndFlush(T entity);

	/**
	 * Update and flush.
	 *
	 * @param entity the entity
	 * @return the t
	 */
	public T updateAndFlush(T entity);

	/**
	 * Delete and flush.
	 *
	 * @param entity the entity
	 */
	public void deleteAndFlush(T entity);

	/**
	 * Save all.
	 *
	 * @param listT the list T
	 */
	public void saveAll(Collection<T> listT);

	/**
	 * Flush.
	 */
	public void flush();

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	public void deleteById(ID id);


	/**
	 * Find by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the list
	 */
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter);


	/**
	 * Count by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the long
	 */
	public Long countByFilter(QueryFilter<T, ID> queryFilter);
	
	
	/**
	 * Find by filter.
	 *
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the list
	 */
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter,String sql);


	/**
	 * Count by filter.
	 *
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the long
	 */
	public Long countByFilter(QueryFilter<T, ID> queryFilter,String sql);	
	
	
	/**
	 * Delete by filter.
	 *
	 * @param queryFilter the query filter
	 */
	public void deleteByFilter(QueryFilter<T, ID> queryFilter);

	/**
	 * Find single result by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the t
	 */
	public T findSingleResultByFilter(QueryFilter<T, ID> queryFilter);

	/**
	 * Find single result by filter.
	 *
	 * @param queryFilter the query filter
	 * @param select      the select
	 * @return the t
	 */
	public T findSingleResultByFilter(QueryFilter<T, ID> queryFilter, String select);
	

	/**
	 * Map find by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the map
	 */
	public Map<ID,T> mapFindByFilter(QueryFilter<T, ID> queryFilter);
	
	

	/**
	 * Map find by filter.
	 *
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the map
	 */
	public Map<ID,T> mapFindByFilter(QueryFilter<T, ID> queryFilter,String sql);


	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryFilter<T, ID> queryFilter, Class<J> classKey, String key);



	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryFilter<T, ID> queryFilter, String sql, Class<J> classKey, String key);



	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryFilter<T, ID> queryFilter, Class<J> classKey, String key);



	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryFilter<T, ID> queryFilter, String sql, Class<J> classKey, String key);

}
