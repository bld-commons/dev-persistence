/*
 * @auth Francesco Baldi
 * @class bld.commons.service.JpaServiceImpl.java
 */
package bld.commons.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import bld.commons.exception.PropertiesException;
import bld.commons.reflection.model.BuildJpqlQueryParameter;
import bld.commons.reflection.model.BuildNativeQueryParameter;
import bld.commons.reflection.model.NativeQueryParameter;
import bld.commons.reflection.model.QueryParameter;
import bld.commons.reflection.utils.ReflectionCommons;
import bld.commons.utils.PersistenceMap;
import jakarta.persistence.Id;

/**
 * The Class BaseEntityServiceImpl.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("unchecked")
public abstract class JpaServiceImpl<T, ID> extends BaseJpaService<T, ID> implements JpaService<T, ID> {

	/** The Constant POINT. */
	private static final String POINT = "\\.";

	/** The id. */
	private Field id = null;

	/** The reflection commons. */
	@Autowired
	protected ReflectionCommons reflectionCommons;

	/** The query jpl. */
	@Autowired
	protected QueryJpql<T> queryJpl;

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	protected Field getId() {
		return id;
	}

	/**
	 * Instantiates a new jpa service impl.
	 */
	public JpaServiceImpl() {
		super();

		Set<Field> fields = ReflectionCommons.getListField(this.getClassEntity());
		for (Field field : fields)
			if (field.isAnnotationPresent(Id.class)) {
				this.id = field;
				break;
			}

	}

	/**
	 * Gets the jpa repository.
	 *
	 * @return the jpa repository
	 */
	protected abstract JpaRepository<T, ID> getJpaRepository();

	/**
	 * Configure query filter.
	 *
	 * @param mapConditions  the map conditions
	 * @param mapOrders      the map orders
	 * @param queryParameter the query filter
	 * @param query          the query
	 * @return the builds the query filter
	 */
	private BuildJpqlQueryParameter<T, ID> configureQueryParameter(Map<String, String> mapConditions, Map<String, String> mapOrders, QueryParameter<T, ID> queryParameter, String query) {
		if (MapUtils.isEmpty(this.getMapOneToMany())) {
			this.setMapOneToMany();
		}
		if (queryParameter.getBaseParameter() != null)
			queryParameter = reflectionCommons.dataToMap(queryParameter);
		return new BuildJpqlQueryParameter<>(mapConditions, mapOrders, queryParameter, query);
	}

	/**
	 * Sets the map one to many.
	 */
	private void setMapOneToMany() {
		super.setMapOneToMany(this.queryJpl.getMapOneToMany());

	}

	/**
	 * Select by filter.
	 *
	 * @return the string
	 */
	private String selectByFilter() {
		return this.queryJpl.selectByFilter();
	}
	
	/**
	 * Select id by filter.
	 *
	 * @return the string
	 */
	private String selectIdByFilter() {
		return this.queryJpl.selectIdByFilter();
	}
	
	/**
	 * Delete by filter.
	 *
	 * @return the string
	 */
	private String deleteByFilter() {
		return this.queryJpl.deleteByFilter();
	}

	/**
	 * Count by filter.
	 *
	 * @return the string
	 */
	private String countByFilter() {
		return this.queryJpl.countByFilter();
	}


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
	 * Delete all.
	 *
	 * @param entities the entities
	 */
	@Override
	public void deleteAll(Collection<T> entities) {
		if (CollectionUtils.isNotEmpty(entities))
			this.getJpaRepository().deleteAll(entities);
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
	 * @param key  the key
	 * @param join the join
	 */
	protected void addJoinOneToMany(String key, String... join) {
		if (!this.getMapOneToMany().containsKey(key)) {
			this.getMapOneToMany().put(key, new LinkedHashSet<>());
		}
		this.getMapOneToMany().get(key).addAll(Arrays.asList(join));
	}

	/**
	 * Find by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the list
	 */
	@Override
	public List<T> findByFilter(QueryParameter<T, ID> queryParameter) {
		return this.findByFilter(queryParameter, selectByFilter());
	}

	/**
	 * Find single result by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the t
	 */
	@Override
	public T singleResultByFilter(QueryParameter<T, ID> queryParameter) {
		return this.singleResultByFilter(queryParameter, selectByFilter());
	}

	/**
	 * Find single result by filter.
	 *
	 * @param queryParameter the query filter
	 * @param select         the select
	 * @return the t
	 */
	@Override
	public T singleResultByFilter(QueryParameter<T, ID> queryParameter, String select) {
		BuildJpqlQueryParameter<T, ID> buildQueryFilter = configureQueryParameter(this.queryJpl.mapConditions(), new HashMap<>(), queryParameter, select);
		return super.findSingleResultByFilter(buildQueryFilter);
	}

	/**
	 * Count by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the long
	 */
	@Override
	public Long countByFilter(QueryParameter<T, ID> queryParameter) {
		return this.countByFilter(queryParameter, countByFilter());
	}

	/**
	 * Find by filter.
	 *
	 * @param queryParameter the query filter
	 * @param select         the select
	 * @return the list
	 */
	@Override
	public List<T> findByFilter(QueryParameter<T, ID> queryParameter, String select) {
		BuildJpqlQueryParameter<T, ID> buildQueryFilter = configureQueryParameter(this.queryJpl.mapConditions(), this.queryJpl.mapJpaOrders(), queryParameter, select);
		return super.findByFilter(buildQueryFilter);
	}

	/**
	 * Count by filter.
	 *
	 * @param queryParameter the query filter
	 * @param count          the count
	 * @return the long
	 */
	@Override
	public Long countByFilter(QueryParameter<T, ID> queryParameter, String count) {
		BuildJpqlQueryParameter<T, ID> buildQueryFilter = configureQueryParameter(this.queryJpl.mapConditions(), new HashMap<>(), queryParameter, count);
		return super.countByFilter(buildQueryFilter);
	}

	/**
	 * Delete by filter.
	 *
	 * @param queryParameter the query filter
	 */
	@Override
	public void deleteByFilter(QueryParameter<T, ID> queryParameter) {
		BuildJpqlQueryParameter<T, ID> buildQueryFilter = configureQueryParameter(this.queryJpl.mapConditions(), new HashMap<>(), queryParameter, selectIdByFilter());
		List<ID>ids=super.findIdByFilter(buildQueryFilter);
		if(CollectionUtils.isNotEmpty(ids)) {
			QueryParameter<T, ID> qp=new QueryParameter<>();
			qp.addParameter("id", ids);
			buildQueryFilter = configureQueryParameter(this.queryJpl.mapDeleteConditions(), new HashMap<>(), qp, deleteByFilter());
			super.deleteByFilter(buildQueryFilter);
		}
	}

	/**
	 * Map find by filter.
	 *
	 * @param queryParameter the query filter
	 * @return the map
	 */
	@Override
	public Map<ID, T> mapFindByFilter(QueryParameter<T, ID> queryParameter) {
		List<T> list = this.findByFilter(queryParameter);
		return mapIdEntity(list);
	}

	/**
	 * Map id entity.
	 *
	 * @param list the list
	 * @return the map
	 */
	private Map<ID, T> mapIdEntity(List<T> list) {
		Map<ID, T> map = new HashMap<>();
		for (T t : list) {
			try {
				map.put((ID) PropertyUtils.getProperty(t, id.getName()), t);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new PropertiesException(e);
			}
		}

		return map;
	}

	/**
	 * Map find by filter.
	 *
	 * @param queryParameter the query filter
	 * @param sql            the sql
	 * @return the map
	 */
	@Override
	public Map<ID, T> mapFindByFilter(QueryParameter<T, ID> queryParameter, String sql) {
		List<T> list = this.findByFilter(queryParameter, sql);
		return mapIdEntity(list);
	}

	/**
	 * Gets the key.
	 *
	 * @param fields the fields
	 * @param t      the t
	 * @return the key
	 */
	private Object getKey(String[] fields, T t) {
		Object value = t;
		for (String field : fields) {
			try {
				value = PropertyUtils.getProperty(value, field);
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new PropertiesException(e);
			}
		}

		return value;
	}

	/**
	 * Map key entity.
	 *
	 * @param <J>      the generic type
	 * @param list     the list
	 * @param classKey the class key
	 * @param key      the key
	 * @return the persistence map
	 */
	private <J> PersistenceMap<J, T> mapKeyEntity(List<T> list, Class<J> classKey, String key) {
		PersistenceMap<J, T> map = new PersistenceMap<>();
		String[] fields = key.split(POINT);
		for (T t : list)
			map.put((J) getKey(fields, t), t);
		return map;
	}

	/**
	 * Map key find by filter.
	 *
	 * @param <J>            the generic type
	 * @param queryParameter the query filter
	 * @param classKey       the class key
	 * @param key            the key
	 * @return the persistence map
	 */
	@Override
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryParameter<T, ID> queryParameter, Class<J> classKey, String key) {
		List<T> list = this.findByFilter(queryParameter);
		return mapKeyEntity(list, classKey, key);
	}

	/**
	 * Map key find by filter.
	 *
	 * @param <J>            the generic type
	 * @param queryParameter the query filter
	 * @param sql            the sql
	 * @param classKey       the class key
	 * @param key            the key
	 * @return the persistence map
	 */
	@Override
	public <J> PersistenceMap<J, T> mapKeyFindByFilter(QueryParameter<T, ID> queryParameter, String sql, Class<J> classKey, String key) {
		List<T> list = this.findByFilter(queryParameter, sql);
		return mapKeyEntity(list, classKey, key);
	}

	/**
	 * Map key list entity.
	 *
	 * @param <J>       the generic type
	 * @param list      the list
	 * @param classKey  the class key
	 * @param keyFields the key fields
	 * @return the persistence map
	 */
	private <J> PersistenceMap<J, List<T>> mapKeyListEntity(List<T> list, Class<J> classKey, String keyFields) {
		PersistenceMap<J, List<T>> map = new PersistenceMap<>();
		String[] fields = keyFields.split(POINT);
		for (T t : list) {
			J key = (J) getKey(fields, t);
			if (!map.containsKey(key))
				map.put(key, new ArrayList<>());
			map.get((J) getKey(fields, t)).add(t);
		}

		return map;
	}

	/**
	 * Map key list find by filter.
	 *
	 * @param <J>            the generic type
	 * @param queryParameter the query filter
	 * @param classKey       the class key
	 * @param key            the key
	 * @return the persistence map
	 */
	@Override
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryParameter<T, ID> queryParameter, Class<J> classKey, String key) {
		List<T> list = this.findByFilter(queryParameter);
		return mapKeyListEntity(list, classKey, key);
	}

	/**
	 * Map key list find by filter.
	 *
	 * @param <J>            the generic type
	 * @param queryParameter the query filter
	 * @param sql            the sql
	 * @param classKey       the class key
	 * @param key            the key
	 * @return the persistence map
	 */
	@Override
	public <J> PersistenceMap<J, List<T>> mapKeyListFindByFilter(QueryParameter<T, ID> queryParameter, String sql, Class<J> classKey, String key) {
		List<T> list = this.findByFilter(queryParameter, sql);
		return mapKeyListEntity(list, classKey, key);
	}


	/**
	 * Find by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the list
	 */
	@Override
	public <K> List<K> findByFilter(NativeQueryParameter<K, ID> queryParameter, String sql) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, sql);
		return super.findByFilter(buildQueryFilter);

	}


	/**
	 * Count by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param count the count
	 * @return the long
	 */
	@Override
	public <K> Long countByFilter(NativeQueryParameter<K, ID> queryParameter, String count) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, count);
		return this.nativeQueryCountByFilter(buildQueryFilter);
	}


	/**
	 * Gets the builds the native query filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the builds the native query filter
	 */
	private <K> BuildNativeQueryParameter<K, ID> getBuildNativeQueryFilter(NativeQueryParameter<K, ID> queryParameter, String sql) {
		if (queryParameter.getBaseParameter() != null)
			queryParameter = reflectionCommons.dataToMap(queryParameter);
		BuildNativeQueryParameter<K, ID> buildQueryFilter = new BuildNativeQueryParameter<>(this.queryJpl.mapNativeConditions(), this.queryJpl.mapNativeOrders(), queryParameter, sql);
		return buildQueryFilter;
	}


	/**
	 * Single result by filter.
	 *
	 * @param <K> the key type
	 * @param queryParameter the query parameter
	 * @param sql the sql
	 * @return the k
	 */
	@Override
	public <K> K singleResultByFilter(NativeQueryParameter<K, ID> queryParameter, String sql) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, sql);
		List<K> list = this.findByFilter(buildQueryFilter);
		K k = null;
		if (list.size() > 1)
			throw new RuntimeException("Find multiple record");
		else if (list != null)
			k = list.get(0);
		return k;
	}

}
