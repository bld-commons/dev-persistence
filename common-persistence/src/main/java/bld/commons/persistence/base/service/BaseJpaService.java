package bld.commons.persistence.base.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import bld.commons.persistence.reflection.model.BuildQueryFilter;
import bld.commons.persistence.reflection.model.QueryFilter;
import bld.commons.persistence.reflection.utils.ReflectionUtils;

/**
 * The Class BaseJpaService.
 */
public abstract class BaseJpaService  {

	/** The Constant FETCH. */
	private static final String fetch = "(?i)fetch";
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(BaseJpaService.class);
	/** The Constant ORDER_BY. */
	private static final String ORDER_BY = "order by";
	
	/** The Constant ONE_TO_MANY. */
	public static final CharSequence ONE_TO_MANY = "<ONE_TO_MANY>";

	/**
	 * Gets the entity manager.
	 *
	 * @return the entity manager
	 */
	protected abstract EntityManager getEntityManager();

	/**
	 * Gets the jdbc template.
	 *
	 * @return the jdbc template
	 */
	protected abstract NamedParameterJdbcTemplate getJdbcTemplate();
	
	/** The reflection utils. */
	@Autowired	
	protected ReflectionUtils reflectionUtils;

	/**
	 * Instantiates a new base jpa service.
	 */
	public BaseJpaService() {
		super();
	}

	/**
	 * Gets the unit name.
	 *
	 * @return the unit name
	 */
	protected String getUnitName() {
		return null;
	};

	/**
	 * Gets the where condition.
	 *
	 * @param mapParameters the map parameters
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @return the where condition
	 */
	private String getWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions) {
		for (String key : mapParameters.keySet()) {
			String val = mapConditions.get(key);
			logger.info("Key: " + key + " Parameter: " + val);
			select += val;
		}
		return select;
	}

	/**
	 * Sets the query parameters.
	 *
	 * @param <T> the generic type
	 * @param mapParameters the map parameters
	 * @param query the query
	 * @return the t
	 */
	private <T extends Query> T setQueryParameters(Map<String, Object> mapParameters, T query) {
		for (String key : mapParameters.keySet())
			query.setParameter(key, mapParameters.get(key));
		return query;
	}

	/**
	 * Gets the where condition null or not null.
	 *
	 * @param checkNullable the check nullable
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @return the where condition null or not null
	 */
	private String getWhereConditionNullOrNotNull(Set<String> checkNullable, String select,Map<String, String> mapConditions) {
		if (checkNullable != null) {
			for (String isNullAndIsNotNull : checkNullable) {
				logger.info("String isNullAndIsNotNull: " + isNullAndIsNotNull);
				select += mapConditions.get(isNullAndIsNotNull);
			}
		}
		return select;
	}

	/**
	 * Delete by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 */
	public <T, ID> void deleteByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String delete = makeQuery(queryFilter.getMapParameters(),buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(),queryFilter.getCheckNullable());
		logger.info("Query= " + delete);
		TypedQuery<?> query = (TypedQuery<?>) this.getEntityManager().createQuery(delete);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		query.executeUpdate();
	}

	/**
	 * Find by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	public <T, ID> List<T> findByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		Map<String, Object> mapParameters = queryFilter.getMapParameters();
		String select = makeQuery(mapParameters, buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable());
		select = addRelationshipsOneToMany(mapParameters, select, buildQueryFilter.getMapOneToMany(),queryFilter.getCheckNullable());
		String orderBy = getOrderBy(queryFilter.getSortKey(), queryFilter.getSortOrder());
		if (StringUtils.isNotBlank(orderBy)) {
			if (select.contains(ORDER_BY)) {
				if (orderBy != null && !orderBy.isEmpty())
					select += "," + orderBy;
			} else
				select += " order by " + orderBy;
		}
		logger.info("Query= " + select);
		TypedQuery<T> query = this.getEntityManager().createQuery(select, queryFilter.getClassFilter());
		query = setQueryParameters(mapParameters, query);
		if (queryFilter.getPageable() != null) {
			query.setFirstResult(queryFilter.getPageable().getPageNumber() * queryFilter.getPageable().getPageSize());
			query.setMaxResults(queryFilter.getPageable().getPageSize());
		}
		return query.getResultList();
	}

	/**
	 * Make query.
	 *
	 * @param mapParameters the map parameters
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @param checkNullable the check nullable
	 * @return the string
	 */
	private String makeQuery(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions,Set<String> checkNullable) {
		select = getWhereConditionNullOrNotNull(checkNullable, select, mapConditions);
		select = getWhereCondition(mapParameters, select, mapConditions);
		return select;
	}

	/**
	 * Gets the order by.
	 *
	 * @param sortKey the sort key
	 * @param sortOrder the sort order
	 * @return the order by
	 */
	private String getOrderBy(String sortKey, String sortOrder) {
		String orderBy = "";
		if (StringUtils.isNotBlank(sortKey)) {
			orderBy = sortKey;
			if (StringUtils.isNotBlank(sortOrder))
				orderBy += " " + sortOrder;
		}
		return orderBy;
	}
	
	
	

	/**
	 * Adds the relationships one to many.
	 *
	 * @param mapParameters           the map parametri
	 * @param select                 the select
	 * @param mapOneToMany the map one to many
	 * @param checkNullable the check nullable
	 * @return the string
	 */
	private String addRelationshipsOneToMany(Map<String, Object> mapParameters, String select,Map<String, LinkedHashSet<String>> mapOneToMany, Set<String> checkNullable) {
		String innerJoin = " ";
		Set<String> listJoin=new HashSet<>();
		
		for (String key : mapOneToMany.keySet()) {
			if (mapParameters.containsKey(key) || (checkNullable != null && checkNullable.contains(key))) {
				Set<String> joins = mapOneToMany.get(key);
				for(String join:joins) {
					join=ReflectionUtils.removeExtraSpace(join);
					if(!listJoin.contains(join)) {
						listJoin.add(join);
						innerJoin += " "+join+" ";
					}
				}
			}
		}
		
		select = select.replace(ONE_TO_MANY, innerJoin);
		logger.info("------------------------Replace select-------------------------------------");
		return select;
	}

	/**
	 * Count by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public <T,ID> Long countByFilter(BuildQueryFilter<T,ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String count = buildQueryFilter.getSql().replaceAll(fetch, "");
		count = addRelationshipsOneToMany(queryFilter.getMapParameters(), count, buildQueryFilter.getMapOneToMany(),queryFilter.getCheckNullable());
		count = makeQuery(queryFilter.getMapParameters(), count, buildQueryFilter.getMapConditions(),queryFilter.getCheckNullable());
		Query query = this.getEntityManager().createQuery(count, Long.class);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		return (Long) query.getSingleResult();
	}

	/**
	 * Find by id.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public <T, ID> T findById(BuildQueryFilter<T,ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		Map<String, Object> mapParameters = new HashMap<>();
		mapParameters.put(QueryFilter.ID, queryFilter.getId());
		queryFilter.setMapParameters(mapParameters);
		T entity = null;
		List<T> listEntity = this.findByFilter(buildQueryFilter);
		if (!listEntity.isEmpty())
			entity = listEntity.get(0);
		return entity;
	}

	

	/**
	 * Jdbc select by filter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 * @throws Exception the exception
	 */
	public <T,ID> List<T> jdbcSelectByFilter(BuildQueryFilter<T, ID>buildQueryFilter)
			throws Exception {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String orderBy = getOrderBy(queryFilter.getSortKey(), queryFilter.getSortOrder());
		String select = makeQuery(queryFilter.getMapParameters(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable());
		if (orderBy != null && !orderBy.isEmpty()) {
			if (select.contains(ORDER_BY)) {
				if (orderBy != null && !orderBy.isEmpty())
					select += "," + orderBy;
			} else
				select += " order by " + orderBy;
		}
		buildQueryFilter.setSql(select);
		return this.jdbcSelect(buildQueryFilter);
	}

	/**
	 * Jdbc select.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 * @throws Exception the exception
	 */
	public <T, ID> List<T> jdbcSelect(BuildQueryFilter<T, ID>buildQueryFilter)
			throws Exception {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String select=buildQueryFilter.getSql();
		logger.info("Query: " +select);
		List<Map<String, Object>> listResult = this.getJdbcTemplate().queryForList(select, queryFilter.getMapParameters());
		List<T> listT = new ArrayList<T>();
		for (Map<String, Object> mapResult : listResult) {
			T t = queryFilter.getClassFilter().getConstructor().newInstance();
			this.reflectionUtils.reflection(t, mapResult);
			listT.add(t);
		}
		return listT;
	}

}