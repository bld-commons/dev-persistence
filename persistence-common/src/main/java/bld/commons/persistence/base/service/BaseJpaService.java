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

public abstract class BaseJpaService  {

	/** The Constant FETCH. */
	private static final String fetch = "(?i)fetch";
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(BaseJpaService.class);
	/** The Constant ORDER_BY. */
	private static final String ORDER_BY = "order by";
	public static final CharSequence ONE_TO_MANY = "<ONE_TO_MANY>";

	protected abstract EntityManager getEntityManager();

	protected abstract NamedParameterJdbcTemplate getJdbcTemplate();
	
	@Autowired	
	protected ReflectionUtils reflectionUtils;

	public BaseJpaService() {
		super();
	}

	protected String getUnitName() {
		return null;
	};

	private String getWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions) {
		for (String key : mapParameters.keySet()) {
			String val = mapConditions.get(key);
			logger.info("Key: " + key + " Parameter: " + val);
			select += val;
		}
		return select;
	}

	private <T extends Query> T setQueryParameters(Map<String, Object> mapParameters, T query) {
		for (String key : mapParameters.keySet())
			query.setParameter(key, mapParameters.get(key));
		return query;
	}

	private String getWhereConditionNullOrNotNull(Set<String> checkNullable, String select,Map<String, String> mapConditions) {
		if (checkNullable != null) {
			for (String isNullAndIsNotNull : checkNullable) {
				logger.info("String isNullAndIsNotNull: " + isNullAndIsNotNull);
				select += mapConditions.get(isNullAndIsNotNull);
			}
		}
		return select;
	}

	public <T, ID> void deleteByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String delete = makeQuery(queryFilter.getMapParameters(),buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(),queryFilter.getCheckNullable());
		logger.info("Query= " + delete);
		TypedQuery<?> query = (TypedQuery<?>) this.getEntityManager().createQuery(delete);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		query.executeUpdate();
	}

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

	private String makeQuery(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions,Set<String> checkNullable) {
		select = getWhereConditionNullOrNotNull(checkNullable, select, mapConditions);
		select = getWhereCondition(mapParameters, select, mapConditions);
		return select;
	}

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
	 * @param mapListKeyOneToMany    the map list key one to many
	 * @param checkNullable
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

	public <T,ID> Long countByFilter(BuildQueryFilter<T,ID> buildQueryFilter) {
		QueryFilter<T,ID>queryFilter=buildQueryFilter.getQueryFilter();
		String count = buildQueryFilter.getSql().replaceAll(fetch, "");
		count = addRelationshipsOneToMany(queryFilter.getMapParameters(), count, buildQueryFilter.getMapOneToMany(),queryFilter.getCheckNullable());
		count = makeQuery(queryFilter.getMapParameters(), count, buildQueryFilter.getMapConditions(),queryFilter.getCheckNullable());
		Query query = this.getEntityManager().createQuery(count, Long.class);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		return (Long) query.getSingleResult();
	}

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