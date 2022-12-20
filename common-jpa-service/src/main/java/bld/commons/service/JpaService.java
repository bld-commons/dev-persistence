/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JpaService.java
 */
package bld.commons.service;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import bld.commons.reflection.model.NativeQueryParameter;
import bld.commons.reflection.model.QueryParameter;
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
	 * @param queryParameter the query filter
	 * @return the list
	 */
	public List<T> findByFilter(QueryParameter<T, ID> queryParameter);


	/**
	 * Count by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the long
	 */
	public Long countByFilter(QueryParameter<T, ID> queryParameter);
	
	
	/**
	 * Find by filter.
	 *
	 * @param queryParameter the query filter
	 * @param sql the sql
	 * @return the list
	 */
	public List<T> findByFilter(QueryParameter<T, ID> queryParameter,String sql);


	/**
	 * Count by filter.
	 *
	 * @param queryParameter the query filter
	 * @param sql the sql
	 * @return the long
	 */
	public Long countByFilter(QueryParameter<T, ID> queryParameter,String sql);	
	
	
	/**
	 * Delete by filter.
	 *
	 * @param queryParameter the query filter
	 */
	public void deleteByFilter(QueryParameter<T, ID> queryParameter);

	/**
	 * Find single result by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the t
	 */
	public T findSingleResultByFilter(QueryParameter<T, ID> queryParameter);

	/**
	 * Find single result by filter.
	 *
	 * @param queryParameter the query filter
	 * @param select      the select
	 * @return the t
	 */
	public T findSingleResultByFilter(QueryParameter<T, ID> queryParameter, String select);
	

	/**
	 * Map find by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the map
	 */
	public Map<ID,T> mapFindByFilter(QueryParameter<T, ID> queryParameter);
	
	

	/**
	 * Map find by filter.
	 *
	 * @param queryParameter the query filter
	 * @param sql the sql
	 * @return the map
	 */
	public Map<ID,T> mapFindByFilter(QueryParameter<T, ID> queryParameter,String sql);


	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryParameter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryParameter<T, ID> queryParameter, Class<J> classKey, String key);



	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryParameter the query filter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryParameter<T, ID> queryParameter, String sql, Class<J> classKey, String key);



	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryParameter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryParameter<T, ID> queryParameter, Class<J> classKey, String key);



	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the persistence map
	 */
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryParameter<T, ID> queryParameter, String sql, Class<J> classKey, String key);

	/**
	 * Native query find by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the list
	 */
	public <K> List<K> nativeQueryFindByFilter(NativeQueryParameter<K, ID> queryParameter, String sql);

	/**
	 * Native query count by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query filter
	 * @param count the count
	 * @return the long
	 */
	public <K> Long nativeQueryCountByFilter(NativeQueryParameter<K, ID> queryParameter, String count);
	
	/**
	 * Native query single result by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the k
	 */
	public <K> K nativeQuerySingleResultByFilter(NativeQueryParameter<K, ID> queryParameter, String sql);

//	public T findSingleResultByFilter(QueryParameter<T, ID> queryParameter, StringBuilder select);
//
//	public Long countByFilter(QueryParameter<T, ID> queryParameter, StringBuilder count);
//
//	public List<T> findByFilter(QueryParameter<T, ID> queryParameter, StringBuilder select);

}
