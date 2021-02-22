/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JpaServiceImpl.java
 */
package bld.commons.persistence.base.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import bld.commons.persistence.reflection.model.BuildQueryFilter;
import bld.commons.persistence.reflection.model.QueryFilter;
import bld.commons.persistence.reflection.utils.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseEntityServiceImpl.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
public abstract class JpaServiceImpl<T, ID> extends BaseJpaService implements JpaService<T, ID> {

	/** The classe. */
	protected Class<T> clazz = ReflectionUtils.getGenericTypeClass(this);

	/** The reflection utils. */
	@Autowired
	private ReflectionUtils reflectionUtils;

	/**
	 * Gets the jpa repository.
	 *
	 * @return the jpa repository
	 */
	protected abstract JpaRepository<T, ID> getJpaRepository();

	/**
	 * Configure query filter.
	 *
	 * @param queryFilter the query filter
	 * @param query the query
	 * @return the builds the query filter
	 */
	private BuildQueryFilter<T, ID> configureQueryFilter(QueryFilter<T, ID> queryFilter, String query) {
		if (MapUtils.isEmpty(this.mapOneToMany)) {
			this.mapOneToMany=new HashMap<>();
			mapOneToMany();
		}
		if(queryFilter.getParameterFilter()!=null)
			queryFilter=reflectionUtils.dataToMap(queryFilter);
		queryFilter.setClassFilter(clazz);
		return baseConfigureQueryFilter(queryFilter, query);
	}

	/**
	 * Base configure query filter.
	 *
	 * @param queryFilter the query filter
	 * @param query the query
	 * @return the builds the query filter
	 */
	private BuildQueryFilter<T, ID> baseConfigureQueryFilter(QueryFilter<T, ID> queryFilter, String query) {
		return new BuildQueryFilter<>(mapConditions(), queryFilter, query);
	}
	
	
	/**
	 * Select by filter.
	 *
	 * @return the string
	 */
	protected abstract String selectByFilter();

	/**
	 * Count by filter.
	 *
	 * @return the string
	 */
	protected abstract String countByFilter();


	/**
	 * Map condizioni.
	 *
	 * @return the map
	 */
	protected abstract Map<String, String> mapConditions();

	/**
	 * Count.
	 *
	 * @return the long
	 */
	@Override
	public long count() {
		return this.getJpaRepository().count();
	}

	/**
	 * Delete.
	 *
	 * @param entity the entity
	 */
	@Override
	public void delete(T entity) {
		this.getJpaRepository().delete(entity);
	}

	/**
	 * Find by id.
	 *
	 * @param id the id
	 * @return the t
	 */
	@Override
	public T findById(ID id) {
		return this.getJpaRepository().findById(id).orElse(null);
	}

	/**
	 * Find all.
	 *
	 * @return the list
	 */
	@Override
	public List<T> findAll() {
		return this.getJpaRepository().findAll();
	}

	/**
	 * Save.
	 *
	 * @param entity the entity
	 */
	@Override
	public void save(T entity) {
		this.getJpaRepository().save(entity);

	}

	/**
	 * Update.
	 *
	 * @param entity the entity
	 * @return the t
	 */
	@Override
	public T update(T entity) {
		return this.getJpaRepository().save(entity);
	}

	/**
	 * Save and flush.
	 *
	 * @param entity the entity
	 */
	@Override
	public void saveAndFlush(T entity) {
		this.getJpaRepository().saveAndFlush(entity);
	}

	/**
	 * Update and flush.
	 *
	 * @param entity the entity
	 * @return the t
	 */
	@Override
	public T updateAndFlush(T entity) {
		this.getJpaRepository().save(entity);
		this.getJpaRepository().flush();
		return entity;
	}

	/**
	 * Delete and flush.
	 *
	 * @param entity the entity
	 */
	@Override
	public void deleteAndFlush(T entity) {
		this.getJpaRepository().delete(entity);
		this.getJpaRepository().flush();
	}

	/**
	 * Save all.
	 *
	 * @param listT the list T
	 */
	@Override
	public void saveAll(Collection<T> listT) {
		if (!CollectionUtils.isEmpty(listT))
			this.getJpaRepository().saveAll(listT);
	}

	/**
	 * Flush.
	 */
	@Override
	public void flush() {
		this.getJpaRepository().flush();
	}

	/**
	 * Delete by id.
	 *
	 * @param id the id
	 */
	@Override
	public void deleteById(ID id) {
		this.getJpaRepository().deleteById(id);
	}

	/**
	 * Adds the join one to many.
	 *
	 * @param key the key
	 * @param join the join
	 */
	protected void addJoinOneToMany(String key, String join) {
		if (!this.mapOneToMany.containsKey(key)) {
			this.mapOneToMany.put(key, new LinkedHashSet<>());
		}
		this.mapOneToMany.get(key).add(join);
	}

	/**
	 * Find by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the list
	 */
	@Override
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter) {
		return this.findByFilter(queryFilter,selectByFilter());
	}

	/**
	 * Count by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the long
	 */
	@Override
	public Long countByFilter(QueryFilter<T, ID> queryFilter) {
		return this.countByFilter(queryFilter, countByFilter());
	}
	
	
	/**
	 * Find by filter.
	 *
	 * @param queryFilter the query filter
	 * @param select the select
	 * @return the list
	 */
	@Override
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter,String select) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(queryFilter, select);
		return super.findByFilter(buildQueryFilter);
	}

	/**
	 * Count by filter.
	 *
	 * @param queryFilter the query filter
	 * @param count the count
	 * @return the long
	 */
	@Override
	public Long countByFilter(QueryFilter<T, ID> queryFilter,String count) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(queryFilter, count);
		return super.countByFilter(buildQueryFilter);
	}
	
	
	/**
	 * Delete by filter.
	 *
	 * @param queryFilter the query filter
	 * @param delete the delete
	 */
	@Override
	public void deleteByFilter(QueryFilter<T, ID> queryFilter,String delete) {
		BuildQueryFilter<T, ID> buildQueryFilter = baseConfigureQueryFilter(queryFilter, delete);
		super.deleteByFilter(buildQueryFilter);
	}

}
