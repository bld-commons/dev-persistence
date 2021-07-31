/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.JpaServiceImpl.java
 */
package bld.commons.service;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.Id;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;

import bld.commons.reflection.model.BuildQueryFilter;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.reflection.utils.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseEntityServiceImpl.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("unchecked")
public abstract class JpaServiceImpl<T, ID> extends BaseJpaService implements JpaService<T, ID> {

	/** The Constant POINT. */
	private static final String POINT = "\\.";


	/** The classe. */
	private Class<T> clazz=null;
	
	
	/** The id. */
	private Field id=null;

	/** The reflection utils. */
	@Autowired
	protected ReflectionUtils reflectionUtils;
	
	
	
	

	/**
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	protected Class<T> getClazz() {
		return clazz;
	}

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
		this.clazz = ReflectionUtils.getGenericTypeClass(this);
		Set<Field> fields=ReflectionUtils.getListField(this.clazz);
		for(Field field:fields) 
			if(field.isAnnotationPresent(Id.class)) {
				this.id=field;
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
	 * @param mapConditions the map conditions
	 * @param queryFilter the query filter
	 * @param query the query
	 * @return the builds the query filter
	 */
	private BuildQueryFilter<T, ID> configureQueryFilter(Map<String,String>mapConditions,QueryFilter<T, ID> queryFilter, String query) {
		if (MapUtils.isEmpty(this.mapOneToMany)) {
			this.mapOneToMany=new HashMap<>();
			mapOneToMany();
		}
		if(queryFilter.getFilterParameter()!=null)
			queryFilter=reflectionUtils.dataToMap(queryFilter);
		queryFilter.setResultClass(clazz);
		return new BuildQueryFilter<>(mapConditions, queryFilter, query);
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
	 * Delete by filter.
	 *
	 * @return the string
	 */
	protected abstract String deleteByFilter();


	/**
	 * Map condizioni.
	 *
	 * @return the map
	 */
	protected abstract Map<String, String> mapConditions();

	/**
	 * Map delete conditions.
	 *
	 * @return the map
	 */
	protected abstract Map<String, String> mapDeleteConditions();
	
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
	protected void addJoinOneToMany(String key, String... join) {
		if (!this.mapOneToMany.containsKey(key)) {
			this.mapOneToMany.put(key, new LinkedHashSet<>());
		}
		this.mapOneToMany.get(key).addAll(Arrays.asList(join));
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
	 * Find single result by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the t
	 */
	@Override
	public  T findSingleResultByFilter(QueryFilter<T, ID> queryFilter) {
		return this.findSingleResultByFilter(queryFilter,selectByFilter());
	}
	
	
	/**
	 * Find single result by filter.
	 *
	 * @param queryFilter the query filter
	 * @param select      the select
	 * @return the t
	 */
	@Override
	public T findSingleResultByFilter(QueryFilter<T, ID> queryFilter,String select) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(mapConditions(),queryFilter, select);
		return super.findSingleResultByFilter(buildQueryFilter);
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
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(mapConditions(),queryFilter, select);
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
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(mapConditions(),queryFilter, count);
		return super.countByFilter(buildQueryFilter);
	}
	
	
	/**
	 * Delete by filter.
	 *
	 * @param queryFilter the query filter
	 */
	@Override
	public void deleteByFilter(QueryFilter<T, ID> queryFilter) {
		BuildQueryFilter<T, ID> buildQueryFilter = configureQueryFilter(mapDeleteConditions(), queryFilter, deleteByFilter());
		super.deleteByFilter(buildQueryFilter);
	}
	
	
	/**
	 * Map find by filter.
	 *
	 * @param queryFilter the query filter
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public Map<ID,T> mapFindByFilter(QueryFilter<T, ID> queryFilter) throws Exception{
		List<T>list=this.findByFilter(queryFilter);
		return mapIdEntity(list);
	}

	/**
	 * Map id entity.
	 *
	 * @param list the list
	 * @return the map
	 * @throws IllegalAccessException the illegal access exception
	 * @throws InvocationTargetException the invocation target exception
	 * @throws NoSuchMethodException the no such method exception
	 */
	private Map<ID, T> mapIdEntity(List<T> list) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		Map<ID,T> map=new HashMap<>();
		for(T t:list) 
			map.put((ID) PropertyUtils.getProperty(t, id.getName()),t);
		return map;
	}
	
	
	/**
	 * Map find by filter.
	 *
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public Map<ID,T> mapFindByFilter(QueryFilter<T, ID> queryFilter,String sql) throws Exception{
		List<T>list=this.findByFilter(queryFilter,sql);
		return mapIdEntity(list);
	}

	
	/**
	 * Gets the key.
	 *
	 * @param fields the fields
	 * @param t the t
	 * @return the key
	 * @throws Exception the exception
	 */
	private Object getKey(String[] fields,T t) throws Exception {
		Object value=t;
		for(String field:fields)
			value=PropertyUtils.getProperty(value, field);
		return value;
	}
	
	/**
	 * Map key entity.
	 *
	 * @param <J> the generic type
	 * @param list the list
	 * @param classKey the class key
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 */
	private <J>Map<J, T> mapKeyEntity(List<T> list,Class<J>classKey,String key) throws Exception {
		Map<J,T> map=new LinkedHashMap<>();
		String[] fields=key.split(POINT);
		for(T t:list) 
			map.put((J)getKey(fields, t),t);
		return map;
	}
	
	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public <J> Map<J,T> mapKeyFindByFilter(QueryFilter<T, ID> queryFilter,Class<J>classKey,String key) throws Exception{
		List<T>list=this.findByFilter(queryFilter);
		return mapKeyEntity(list, classKey, key);
	}
	
	/**
	 * Map key find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public <J> Map<J,T> mapKeyFindByFilter(QueryFilter<T, ID> queryFilter,String sql,Class<J>classKey,String key) throws Exception{
		List<T>list=this.findByFilter(queryFilter,sql);
		return mapKeyEntity(list, classKey, key);
	}
	
	
	/**
	 * Map key list entity.
	 *
	 * @param <J> the generic type
	 * @param list the list
	 * @param classKey the class key
	 * @param keyFields the key fields
	 * @return the map
	 * @throws Exception the exception
	 */
	private <J>Map<J, List<T>> mapKeyListEntity(List<T> list,Class<J>classKey,String keyFields) throws Exception {
		Map<J,List<T>> map=new LinkedHashMap<>();
		String[] fields=keyFields.split(POINT);
		for(T t:list) {
			J key=(J)getKey(fields, t);
			if(!map.containsKey(key))
				map.put(key, new ArrayList<>());
			map.get((J)getKey(fields, t)).add(t);
		}
			
		return map;
	}
	
	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param classKey the class key
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public <J> Map<J,List<T>> mapKeyListFindByFilter(QueryFilter<T, ID> queryFilter,Class<J>classKey,String key) throws Exception{
		List<T>list=this.findByFilter(queryFilter);
		return mapKeyListEntity(list, classKey, key);
	}
	
	/**
	 * Map key list find by filter.
	 *
	 * @param <J> the generic type
	 * @param queryFilter the query filter
	 * @param sql the sql
	 * @param classKey the class key
	 * @param key the key
	 * @return the map
	 * @throws Exception the exception
	 */
	@Override
	public <J> Map<J,List<T>> mapKeyListFindByFilter(QueryFilter<T, ID> queryFilter,String sql,Class<J>classKey,String key) throws Exception{
		List<T>list=this.findByFilter(queryFilter,sql);
		return mapKeyListEntity(list, classKey, key);
	}
}
