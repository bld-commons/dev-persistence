package bld.commons.persistence.base.service;

import java.util.Collection;
import java.util.List;

import bld.commons.persistence.reflection.model.QueryFilter;

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


	public List<T> findByFilter(QueryFilter<T, ID> queryFilter);


	public Long countByFilter(QueryFilter<T, ID> queryFilter);
	
	
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter,String sql);


	public Long countByFilter(QueryFilter<T, ID> queryFilter,String sql);	
	
	
	public void deleteByFilter(QueryFilter<T, ID> queryFilter,String delete);

}
