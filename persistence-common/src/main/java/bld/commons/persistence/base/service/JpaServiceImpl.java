package bld.commons.persistence.base.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.repository.JpaRepository;

import bld.commons.persistence.reflection.model.BuildQueryFilter;
import bld.commons.persistence.reflection.model.QueryFilter;
import bld.commons.persistence.reflection.utils.ReflectionUtils;

/**
 * The Class BaseEntityServiceImpl.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
public abstract class JpaServiceImpl<T, ID> extends BaseJpaService implements JpaService<T, ID> {

	/** The classe. */
	protected Class<T> clazz = ReflectionUtils.getTClass(this);

	private Map<String, LinkedHashSet<String>> mapOneToMany;

	/** The repository. */

	protected abstract JpaRepository<T, ID> getJpaRepository();

	private BuildQueryFilter<T, ID> configureQueryFilter(QueryFilter<T, ID> queryFilter, String query) {
		if (MapUtils.isEmpty(this.mapOneToMany)) {
			this.mapOneToMany=new HashMap<>();
			mapOneToMany();
		}
		return baseConfigureQueryFilter(queryFilter, query);
	}

	private BuildQueryFilter<T, ID> baseConfigureQueryFilter(QueryFilter<T, ID> queryFilter, String query) {
		return new BuildQueryFilter<>(mapConditions(), this.mapOneToMany, queryFilter, query);
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
	 * Gets the map one to one.
	 *
	 * @return the map one to one
	 */
	protected abstract void mapOneToMany();

	/**
	 * Map condizioni.
	 *
	 * @return the map
	 */
	protected abstract Map<String, String> mapConditions();

	@Override
	public long count() {
		return this.getJpaRepository().count();
	}

	@Override
	public void delete(T entity) {
		this.getJpaRepository().delete(entity);
	}

	@Override
	public T findById(ID id) {
		return this.getJpaRepository().findById(id).orElse(null);
	}

	@Override
	public List<T> findAll() {
		return this.getJpaRepository().findAll();
	}

	@Override
	public void save(T entity) {
		this.getJpaRepository().save(entity);

	}

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

	@Override
	public void saveAll(Collection<T> listT) {
		if (!CollectionUtils.isEmpty(listT))
			this.getJpaRepository().saveAll(listT);
	}

	@Override
	public void flush() {
		this.getJpaRepository().flush();
	}

	@Override
	public void deleteById(ID id) {
		this.getJpaRepository().deleteById(id);
	}

	protected void addJoinOneToMany(String key, String join) {
		if (!this.mapOneToMany.containsKey(key)) {
			this.mapOneToMany.put(key, new LinkedHashSet<>());
		}
		this.mapOneToMany.get(key).add(join);
	}

	@Override
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter) {
		return this.findByFilter(queryFilter,selectByFilter());
	}

	@Override
	public Long countByFilter(QueryFilter<T, ID> queryFilter) {
		return this.countByFilter(queryFilter, countByFilter());
	}
	
	
	@Override
	public List<T> findByFilter(QueryFilter<T, ID> queryFilter,String select) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(queryFilter, select);
		return super.findByFilter(buildQueryFilter);
	}

	@Override
	public Long countByFilter(QueryFilter<T, ID> queryFilter,String count) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(queryFilter, count);
		return super.countByFilter(buildQueryFilter);
	}
	
	
	@Override
	public void deleteByFilter(QueryFilter<T, ID> queryFilter,String delete) {
		BuildQueryFilter<T, ID> buildQueryFilter = baseConfigureQueryFilter(queryFilter, delete);
		super.deleteByFilter(buildQueryFilter);
	}

}
