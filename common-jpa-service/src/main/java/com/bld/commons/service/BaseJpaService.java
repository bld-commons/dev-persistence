/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.BaseJpaService.java
 */
package com.bld.commons.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;

import com.bld.commons.exception.JpaServiceException;
import com.bld.commons.exception.OrderByException;
import com.bld.commons.reflection.model.BuildJpqlQueryParameter;
import com.bld.commons.reflection.model.BuildNativeQueryParameter;
import com.bld.commons.reflection.model.ConditionsZoneModel;
import com.bld.commons.reflection.model.OrderBy;
import com.bld.commons.reflection.model.QueryParameter;
import com.bld.commons.reflection.model.TupleParameter;
import com.bld.commons.reflection.utils.ReflectionCommons;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import jakarta.persistence.TypedQuery;

/**
 * The Class BaseJpaService.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("unchecked")
public abstract class BaseJpaService<T, ID> {

	/** The Constant JOIN_ZONE. */
	public final static String JOIN_ZONE = "joinZone";

	/** The Constant END_LINE. */
	private static final String END_LINE = "\n";

	/** The classe. */
	private Class<T> classEntity = null;

	private Class<ID> classId = null;

	/** The Constant FETCH. */
	private static final String fetch = "(?i)fetch";
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(BaseJpaService.class);
	/** The Constant ORDER_BY. */
	private static final String ORDER_BY = " order by ";

	/** The Constant ONE_TO_MANY. */
	public static final CharSequence ONE_TO_MANY = "<ONE_TO_MANY>";

	/** The Constant KEY_PROPERTY. */
	public final static String KEY_PROPERTY = "<PROPERTY>";

	/** The Constant WHERE_1_1. */
	public static final String WHERE_CONDITION = "\nWHERE ";

	/** The map one to many. */
	private Map<String, LinkedHashSet<String>> mapOneToMany;

	/**
	 * Gets the entity manager.
	 *
	 * @return the entity manager
	 */
	protected abstract EntityManager getEntityManager();

	/**
	 * Sets the map one to many.
	 *
	 * @param mapOneToMany the map one to many
	 */
	protected void setMapOneToMany(Map<String, LinkedHashSet<String>> mapOneToMany) {
		this.mapOneToMany = mapOneToMany;
	}

	/**
	 * Gets the map one to many.
	 *
	 * @return the map one to many
	 */
	protected Map<String, LinkedHashSet<String>> getMapOneToMany() {
		return mapOneToMany;
	}

	/** The reflection commons. */
	@Autowired
	protected ReflectionCommons reflectionCommons;

	/**
	 * Instantiates a new base jpa service.
	 */
	public BaseJpaService() {
		super();
		this.classEntity = ReflectionCommons.getGenericTypeClass(this);
		this.classId = ReflectionCommons.getGenericTypeClass(this, 1);
	}

	/**
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	protected Class<T> getClassEntity() {
		return classEntity;
	}

	/**
	 * Sets the query parameters.
	 *
	 * @param <Q>           the generic type
	 * @param mapParameters the map parameters
	 * @param query         the query
	 * @return the q
	 */
	private <Q extends Query> void setQueryParameters(Map<String, Object> mapParameters, Q query) {
		for (Entry<String, Object> entry : mapParameters.entrySet()) {
			Object value = entry.getValue();
			logger.debug("----------------------------------------------------------");
			logger.debug("Key: " + entry.getKey());
			logger.debug("Value: " + value);
			if (value != null)
				logger.debug("Class: " + value.getClass().getName());
			query.setParameter(entry.getKey(), entry.getValue());
		}

	}

	/**
	 * Gets the where condition.
	 *
	 * @param mapParameters the map parameters
	 * @param where         the select
	 * @param mapConditions the map conditions
	 * @return the where condition
	 */
	private void getWhereCondition(Map<String, Object> mapParameters, StringBuilder where, Map<String, String> mapConditions) {
		for (String key : mapParameters.keySet()) 
			this.setCondition(where, mapConditions, key);
	}
	
	private void getWhereTupleCondition(Map<String, TupleParameter> mapParameters, StringBuilder where, Map<String, String> mapConditions) {
		for (Entry<String, TupleParameter> entry : mapParameters.entrySet()) {
			String condition=this.buildTupleCondition(entry.getKey(), mapConditions, entry.getValue());
			where.append(condition);
		}
			
	}


	private void setCondition(StringBuilder where, Map<String, String> mapConditions, String key) {
		String condition = mapConditions.get(key);
		if (StringUtils.isBlank(condition))
			throw new JpaServiceException("The \"" + key + "\" parameter is not mappend in condition");
		where.append(condition);
	}

	/**
	 * Gets the where condition.
	 *
	 * @param nullables     the nullables
	 * @param where         the select
	 * @param mapConditions the map conditions
	 * @return the where condition
	 */
	private void getWhereCondition(Set<String> nullables, StringBuilder where, Map<String, String> mapConditions) {
		if (nullables != null) {
			for (String key : nullables) {
				this.setCondition(where, mapConditions, key);
			}
		}
	}

	/**
	 * Delete by filter.
	 *
	 * @param buildQueryFilter the build query filter
	 */
	public void deleteByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryParameter = buildQueryFilter.getQueryParameter();
		StringBuilder sql = buildQueryFilter.getSql();
		buildWhere(buildQueryFilter, sql);
		queryParameter.mergeParameters();
		final String delete = sql.toString();
		logger.debug("Query= " + delete);
		Query query = this.getEntityManager().createQuery(delete);
		setQueryParameters(queryParameter.getParameters(), query);
		query.executeUpdate();
	}

	public List<ID> findIdByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		TypedQuery<ID> query = buildQuery(buildQueryFilter, this.classId, true);
		return query.getResultList();
	}

	/**
	 * Find by filter.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	public List<T> findByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		TypedQuery<T> query = buildQuery(buildQueryFilter, this.classEntity, false);
		return query.getResultList();
	}

	/**
	 * Builds the query.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the typed query
	 */
	private <J> TypedQuery<J> buildQuery(BuildJpqlQueryParameter<T, ID> buildQueryFilter, Class<J> clazz, boolean removeFetch) {
		QueryParameter<T, ID> queryParameter = buildQueryFilter.getQueryParameter();
		Map<String, Object> mapParameters = queryParameter.getParameters();
		StringBuilder sql = addRelationshipsOneToMany(mapParameters, buildQueryFilter.getSql(), queryParameter.getNullables());
		buildWhere(buildQueryFilter, sql);
		addOrderBy(queryParameter.getListOrderBy(), sql, buildQueryFilter.getMapOrders());
		queryParameter.mergeParameters();
		String select = sql.toString();
		if (removeFetch)
			select = select.replaceAll(fetch, "");
		logger.debug("\nQuery: \n" + select);

		TypedQuery<J> query = this.getEntityManager().createQuery(select, clazz);
		
		setQueryParameters(mapParameters, query);
		if (queryParameter.getPageable() != null) {
			query.setFirstResult(queryParameter.getPageable().getPageNumber() * queryParameter.getPageable().getPageSize());
			query.setMaxResults(queryParameter.getPageable().getPageSize());
		}
		return query;
	}

//	/**
//	 * Gets the class filter parameter.
//	 *
//	 * @param queryFilter the query filter
//	 * @return the class filter parameter
//	 */
//	private  Class<? extends BaseParameter> getClassFilterParameter(QueryParameter<T, ID> queryFilter) {
//		Class<? extends BaseParameter> classFilterParameter = null;
//		if (queryFilter.getBaseParameter() != null)
//			classFilterParameter = (Class<? extends BaseParameter>) queryFilter.getBaseParameter().getClass();
//		return classFilterParameter;
//	}

	/**
	 * Builds the sql.
	 *
	 * @param buildQueryFilter the build query filter
	 * @param sql              the sql
	 * @return the string
	 */
	private void buildWhere(BuildJpqlQueryParameter<T, ID> buildQueryFilter, StringBuilder sql) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		StringBuilder where = new StringBuilder("");
		getWhereCondition(queryFilter.getNullables(), where, buildQueryFilter.getMapConditions());
		getWhereCondition(queryFilter.getParameters(), where, buildQueryFilter.getMapConditions());
		getWhereTupleCondition(queryFilter.getTupleParamenters(), where, buildQueryFilter.getMapConditions());
		if (where.length() > 0)
			sql.append("where\n").append(where.substring(4));

	}

	/**
	 * Adds the order by.
	 *
	 * @param listOrderBy the list order by
	 * @param sql         the sql
	 * @param mapOrders   the map orders
	 */
	private void addOrderBy(List<OrderBy> listOrderBy, StringBuilder sql, Map<String, String> mapOrders) {
		if (!sql.toString().toLowerCase().contains(ORDER_BY.trim()) && CollectionUtils.isNotEmpty(listOrderBy)) {
			StringBuilder writeOrderBy = new StringBuilder("");
			int substring = 0;
			if (CollectionUtils.isNotEmpty(listOrderBy)) {
				for (OrderBy orderBy : listOrderBy) {
					if (!mapOrders.containsKey(orderBy.getSortKey()))
						throw new OrderByException("The \"" + orderBy.getSortKey() + "\" sort key is not found");
					writeOrderBy.append(",").append(mapOrders.get(orderBy.getSortKey())).append(" ").append(orderBy.getOrderType().name());
				}
				substring = 1;
			}
			sql.append(END_LINE).append(ORDER_BY).append(writeOrderBy.substring(substring));
		}

	}

	/**
	 * Adds the relationships one to many.
	 *
	 * @param mapParameters the map parameters
	 * @param select        the select
	 * @param nullables     the nullables
	 * @return the manage one to many
	 */
	private StringBuilder addRelationshipsOneToMany(Map<String, Object> mapParameters, StringBuilder select, Set<String> nullables) {
		StringBuilder innerJoin = new StringBuilder("");
		if (nullables == null)
			nullables = new HashSet<>();
		Set<String> listJoin = new HashSet<>();
		for (String key : this.mapOneToMany.keySet()) {
			if (mapParameters.containsKey(key) || nullables.contains(key)) {
				Set<String> joins = mapOneToMany.get(key);
				for (String join : joins) {
					join = ReflectionCommons.removeExtraSpace(join);
					if (!listJoin.contains(join)) {
						listJoin.add(join);
						innerJoin.append(END_LINE).append(join);
					}
				}
			}
		}
		select.append(innerJoin).append(END_LINE);
		return select;
	}

	/**
	 * Count by filter.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public Long countByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryParameter = buildQueryFilter.getQueryParameter();
		StringBuilder sql = addRelationshipsOneToMany(queryParameter.getParameters(), buildQueryFilter.getSql(), queryParameter.getNullables());
		buildWhere(buildQueryFilter, sql);
		queryParameter.mergeParameters();
		final String count = sql.toString().replaceAll(fetch, "");
		logger.debug("\nQuery: \n" + count);
		Query query = this.getEntityManager().createQuery(count, Long.class);
		setQueryParameters(queryParameter.getParameters(), query);
		return (Long) query.getSingleResult();
	}

	/**
	 * Find single result by filter.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public T findSingleResultByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		TypedQuery<T> query = buildQuery(buildQueryFilter, this.classEntity, false);
		T t = null;
		try {

			t = query.getSingleResult();
		} catch (Exception e) {
			logger.warn("Record not found");
		}
		return t;
	}

	/**
	 * Find by id.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public T findById(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		queryFilter.getParameters().put(QueryParameter.ID, queryFilter.getId());
		return findSingleResultByFilter(buildQueryFilter);
	}

	public <K> List<K> findByFilter(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		StringBuilder sql = buildNativeQuery(buildQueryFilter);
		addOrderBy(buildQueryFilter.getQueryParameter().getListOrderBy(), sql, buildQueryFilter.getMapOrders());
		final String select = sql.toString();
		logger.debug(select);
		Query query = this.getEntityManager().createNativeQuery(select, Tuple.class);
		setNativeQueryParameters(buildQueryFilter.getQueryParameter().getMapConditionsZone(), query);
		if (buildQueryFilter.getQueryParameter().getPageable() != null) {
			query.setFirstResult(buildQueryFilter.getQueryParameter().getPageable().getPageNumber() * buildQueryFilter.getQueryParameter().getPageable().getPageSize());
			query.setMaxResults(buildQueryFilter.getQueryParameter().getPageable().getPageSize());
		}

		List<Tuple> results = query.getResultList();
		List<K> listK = new ArrayList<>();
		for (Tuple row : results) {
			List<TupleElement<?>> elements = row.getElements();
			Map<String, Object> mapRow = new HashMap<>();
			for (TupleElement<?> element : elements) {
				Object value = row.get(element.getAlias());
				if (value != null)
					mapRow.put(element.getAlias(), value);
			}

			K k = this.reflectionCommons.reflection(buildQueryFilter.getQueryParameter().getResultClass(), mapRow);
			listK.add(k);
		}
		return listK;
	}

	/**
	 * Sets the native query parameters.
	 *
	 * @param <Q>     the generic type
	 * @param mapZone the map zone
	 * @param query   the query
	 */
	private <Q extends Query> void setNativeQueryParameters(Map<String, ConditionsZoneModel> mapZone, Q query) {

		for (ConditionsZoneModel zone : mapZone.values()) {
			for (Entry<String, Object> entry : zone.getParameters().entrySet()) {
				logger.debug("Parameter: " + entry.getKey() + "= " + entry.getValue());
				logger.debug("Class: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : " null"));
				query.setParameter(entry.getKey(), entry.getValue());
			}

		}
	}

	/**
	 * Native query count by filter.
	 *
	 * @param <K>              the key type
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public <K> Long countByFilter(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		StringBuilder sql = buildNativeQuery(buildQueryFilter);
		final String count = sql.toString();
		logger.debug(count);
		Query query = this.getEntityManager().createNativeQuery(count);
		setNativeQueryParameters(buildQueryFilter.getQueryParameter().getMapConditionsZone(), query);
		Number size = (Number) query.getSingleResult();
		return size.longValue();
	}

	/**
	 * Builds the native query.
	 *
	 * @param <K>              the key type
	 * @param buildQueryFilter the build query filter
	 * @return the string
	 */
	private <K> StringBuilder buildNativeQuery(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		StringBuilder sql = buildQueryFilter.getSql();
		Set<String> zones = ReflectionCommons.extractVariables(sql.toString());
		Map<String, ConditionsZoneModel> map = new HashMap<>(buildQueryFilter.getQueryParameter().getMapConditionsZone());
		map.remove(JOIN_ZONE);
		Map<String, StringBuilder> mapConditions = buildQueryFilter.getQueryParameter().getEmptyZones();

		for (String key : map.keySet()) {
			ConditionsZoneModel conditionsZoneModel = map.get(key);
			Map<String, String> conditions = buildQueryFilter.getMapConditions().get(key);
			if (!mapConditions.containsKey(key))
				mapConditions.put(key, new StringBuilder(conditionsZoneModel.getWhere()));
			for (String parameter : conditionsZoneModel.getParameters().keySet())
				mapConditions.get(key).append(conditions.get(parameter)).append(END_LINE);
			for (String nullable : conditionsZoneModel.getNullables())
				mapConditions.get(key).append(conditions.get(nullable)).append(END_LINE);
			for (Entry<String, TupleParameter> entry: conditionsZoneModel.getTupleParamenters().entrySet()) {
				String tupleKey=entry.getKey();
				TupleParameter tupleParameter=entry.getValue();
				String condition = buildTupleCondition(tupleKey, conditions, tupleParameter);
				mapConditions.get(key).append(condition).append(END_LINE);
			}
			conditionsZoneModel.mergeParameters();
		}
		StringSubstitutor stringSubstitutor = new StringSubstitutor(mapConditions);
		sql = new StringBuilder(stringSubstitutor.replace(sql));
		for (String zone : zones) {
			zone = "${" + zone + "}";
			int startIndex = sql.indexOf(zone);
			if (startIndex >= 0) {
				int endIndex = startIndex + zone.length();
				sql = sql.replace(startIndex, endIndex, "");
			}

		}

		return sql;
	}

	private String buildTupleCondition(String tupleKey, Map<String, String> conditions, TupleParameter tupleParameter) {
		String condition=conditions.get(tupleKey);
		if (StringUtils.isBlank(condition))
			throw new JpaServiceException("The \"" + tupleKey + "\" parameter is not mappend in condition");
		String refactorCondition="";
		for(int i=0;i<tupleParameter.getObjects().size();i++) 
			refactorCondition+=","+tupleParameter.getCondition(i);
		condition=condition.replace(":"+tupleKey, refactorCondition.substring(1));
		return condition;
	}

}