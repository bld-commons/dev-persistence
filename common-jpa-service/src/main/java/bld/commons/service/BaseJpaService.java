/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.BaseJpaService.java
 */
package bld.commons.service;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
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

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import bld.commons.reflection.annotations.DateFilter;
import bld.commons.reflection.annotations.LikeString;
import bld.commons.reflection.model.BuildQueryFilter;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.OrderBy;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.reflection.type.GetSetType;
import bld.commons.reflection.utils.ReflectionUtils;

// TODO: Auto-generated Javadoc
/**
 * The Class BaseJpaService.
 */
@SuppressWarnings("unchecked")
public abstract class BaseJpaService {

	/** The Constant FETCH. */
	private static final String fetch = "(?i)fetch";
	/** The Constant logger. */
	private static final Log logger = LogFactory.getLog(BaseJpaService.class);
	/** The Constant ORDER_BY. */
	private static final String ORDER_BY = " order by ";

	/** The Constant ONE_TO_MANY. */
	public static final CharSequence ONE_TO_MANY = "<ONE_TO_MANY>";

	/** The map one to many. */
	protected Map<String, LinkedHashSet<String>> mapOneToMany;

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

	/**
	 * Map one to many.
	 */
	protected abstract void mapOneToMany();

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
	 * @param select        the select
	 * @param mapConditions the map conditions
	 * @param classFilterParameter the class filter parameter
	 * @return the where condition
	 */
	private String getWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions,Class<? extends FilterParameter<?>> classFilterParameter) {
		Map<String, LinkedHashSet<Method>>mapMethod=new HashMap<String, LinkedHashSet<Method>>();
		Map<String,Field>mapField=new HashedMap<>();
		if(classFilterParameter!=null) {
			mapMethod=ReflectionUtils.getMapMethod(classFilterParameter);
			mapField=ReflectionUtils.getMapField(classFilterParameter);
		}
		
		for (String key : mapParameters.keySet()) {
			String val = mapConditions.get(key);
			String upper="";
			String minEqual="<=";
			try {
				if(classFilterParameter!=null) {
					Field field=mapField.get(key);
					Method method=ReflectionUtils.getMethod(mapMethod, field, GetSetType.get);
					LikeString likeString=method.isAnnotationPresent(LikeString.class)?method.getAnnotation(LikeString.class):field.getAnnotation(LikeString.class);
					if(likeString!=null && likeString.ignoreCase()) 
						upper="upper";
					else {
						DateFilter dateFilter=method.isAnnotationPresent(LikeString.class)?method.getAnnotation(DateFilter.class):field.getAnnotation(DateFilter.class);
						if(dateFilter!=null && !dateFilter.equals())
							minEqual="<";
					}
				}
				val=val.replace("<upper>", upper).replace("<=", minEqual);
			} catch (Exception e) {
				logger.error("The \""+key+"\" field is not found");
			}
			logger.info("Key: " + key + " Parameter: " + val);
			select += val;
		}
		return select;
	}

	/**
	 * Sets the query parameters.
	 *
	 * @param <T>           the generic type
	 * @param mapParameters the map parameters
	 * @param query         the query
	 * @return the t
	 */
	private <T extends Query> T setQueryParameters(Map<String, Object> mapParameters, T query) {
		for (String key : mapParameters.keySet()) {
			Object value = mapParameters.get(key);
			logger.info("----------------------------------------------------------");
			logger.info("Key: " + key);
			logger.info("Value: " + value);
			logger.info("Class: " + value.getClass().getName());
			query.setParameter(key, mapParameters.get(key));
		}

		return query;
	}

	/**
	 * Gets the where condition null or not null.
	 *
	 * @param checkNullable the check nullable
	 * @param select        the select
	 * @param mapConditions the map conditions
	 * @return the where condition null or not null
	 */
	private String getWhereConditionNullOrNotNull(Set<String> checkNullable, String select, Map<String, String> mapConditions) {
		if (checkNullable != null) {
			for (String key : checkNullable) {
				logger.info("String checkNullable: " + key);
				select += mapConditions.get(key);
			}
		}
		return select;
	}

	/**
	 * Delete by filter.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 */
	
	public <T, ID> void deleteByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String delete = buildQuery(queryFilter.getMapParameters(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable(),getClassFilterParameter(queryFilter));
		logger.info("Query= " + delete);
		TypedQuery<?> query = (TypedQuery<?>) this.getEntityManager().createQuery(delete);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		query.executeUpdate();
	}

	/**
	 * Find by filter.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	public <T, ID> List<T> findByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		TypedQuery<T> query = buildQuery(buildQueryFilter);
		return query.getResultList();
	}

	/**
	 * Builds the query.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the typed query
	 */
	private <T, ID> TypedQuery<T> buildQuery(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		Map<String, Object> mapParameters = queryFilter.getMapParameters();
		String select = buildQuery(mapParameters, buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable(),getClassFilterParameter(queryFilter));
		ManageOneToMany manageOneToMany = addRelationshipsOneToMany(mapParameters, select, queryFilter.getCheckNullable());
		select = manageOneToMany.getSelect();
		select = addOrderBy(queryFilter.getListOrderBy(),select);
		
		logger.info("Query= " + select);
		TypedQuery<T> query = this.getEntityManager().createQuery(select, queryFilter.getClassFilter());
		query = setQueryParameters(mapParameters, query);
		if (manageOneToMany.isOneToMany())
			query.setHint(QueryHints.HINT_PASS_DISTINCT_THROUGH, false);
		if (queryFilter.getPageable() != null) {
			query.setFirstResult(queryFilter.getPageable().getPageNumber() * queryFilter.getPageable().getPageSize());
			query.setMaxResults(queryFilter.getPageable().getPageSize());
		}
		return query;
	}

	/**
	 * Gets the class filter parameter.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param queryFilter the query filter
	 * @return the class filter parameter
	 */
	private <T, ID> Class<? extends FilterParameter<?>> getClassFilterParameter(QueryFilter<T, ID> queryFilter) {
		Class<? extends FilterParameter<?>> classFilterParameter=null;
		if(queryFilter.getFilterParameter()!=null)
			classFilterParameter=(Class<? extends FilterParameter<?>>) queryFilter.getFilterParameter().getClass();
		return classFilterParameter;
	}

	/**
	 * Make query.
	 *
	 * @param mapParameters the map parameters
	 * @param select        the select
	 * @param mapConditions the map conditions
	 * @param checkNullable the check nullable
	 * @param classFilterParameter the class filter parameter
	 * @return the string
	 */
	private String buildQuery(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions, Set<String> checkNullable,Class<? extends FilterParameter<?>>classFilterParameter) {
		select = getWhereConditionNullOrNotNull(checkNullable, select, mapConditions);
		select = getWhereCondition(mapParameters, select, mapConditions,classFilterParameter);
		return select;
	}

	/**
	 * Gets the order by.
	 *
	 * @param listOrderBy the list order by
	 * @param select the select
	 * @return the order by
	 */
	private String addOrderBy(List<OrderBy> listOrderBy, String select) {
		String writeOrderBy = "";
		if(CollectionUtils.isNotEmpty(listOrderBy)) {
			for(OrderBy orderBy:listOrderBy)
				writeOrderBy+=","+orderBy.getSortKey()+" "+orderBy.getOrderType().name();
			writeOrderBy=ORDER_BY+writeOrderBy.substring(1);
		}
		
		if(!select.toLowerCase().contains(ORDER_BY.trim()))
			select += writeOrderBy;
		return select;
	}

	/**
	 * Adds the relationships one to many.
	 *
	 * @param mapParameters the map parametri
	 * @param select        the select
	 * @param checkNullable the check nullable
	 * @return the string
	 */
	private ManageOneToMany addRelationshipsOneToMany(Map<String, Object> mapParameters, String select, Set<String> checkNullable) {
		String innerJoin = " ";
		Set<String> listJoin = new HashSet<>();
		for (String key : this.mapOneToMany.keySet()) {
			if (mapParameters.containsKey(key) || (checkNullable != null && checkNullable.contains(key))) {
				Set<String> joins = mapOneToMany.get(key);
				for (String join : joins) {
					join = ReflectionUtils.removeExtraSpace(join);
					if (!listJoin.contains(join)) {
						listJoin.add(join);
						innerJoin += " " + join + " ";
					}
				}
			}
		}

		select = select.replace(ONE_TO_MANY, innerJoin);
		logger.info("------------------------Replace select-------------------------------------");

		ManageOneToMany manageOneToMany = new ManageOneToMany(select, listJoin.size() > 0);
		return manageOneToMany;
	}

	/**
	 * Count by filter.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public <T, ID> Long countByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String count = buildQueryFilter.getSql().replaceAll(fetch, "");
		ManageOneToMany manageOneToMany =  addRelationshipsOneToMany(queryFilter.getMapParameters(), count, queryFilter.getCheckNullable());
		count=manageOneToMany.getSelect();
		count = buildQuery(queryFilter.getMapParameters(), count, buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable(),getClassFilterParameter(queryFilter));
		Query query = this.getEntityManager().createQuery(count, Long.class);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		return (Long) query.getSingleResult();
	}

	/**
	 * Find single result by filter.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public <T, ID> T findSingleResultByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		TypedQuery<T> query = buildQuery(buildQueryFilter);
		T t = null;
		try {
			t = query.getSingleResult();
		} catch (Exception e) {
			logger.info("Record not found");
		}
		return t;
	}

	/**
	 * Find by id.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public <T, ID> T findById(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		queryFilter.getMapParameters().put(QueryFilter.ID, queryFilter.getId());
		return findSingleResultByFilter(buildQueryFilter);
	}

	/**
	 * Jdbc select by filter.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 * @throws Exception the exception
	 */
	public <T, ID> List<T> jdbcSelectByFilter(BuildQueryFilter<T, ID> buildQueryFilter) throws Exception {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String select = buildQuery(queryFilter.getMapParameters(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions(), queryFilter.getCheckNullable(),getClassFilterParameter(queryFilter));
		select = addOrderBy(queryFilter.getListOrderBy(),select);
	
		buildQueryFilter.setSql(select);
		return this.jdbcSelect(buildQueryFilter);
	}

	/**
	 * Jdbc select.
	 *
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 * @throws Exception the exception
	 */
	public <T, ID> List<T> jdbcSelect(BuildQueryFilter<T, ID> buildQueryFilter) throws Exception {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String select = buildQueryFilter.getSql();
		logger.info("Query: " + select);
		List<Map<String, Object>> listResult = this.getJdbcTemplate().queryForList(select, queryFilter.getMapParameters());
		List<T> listT = new ArrayList<T>();
		for (Map<String, Object> mapResult : listResult) {
			T t = queryFilter.getClassFilter().getConstructor().newInstance();
			this.reflectionUtils.reflection(t, mapResult);
			listT.add(t);
		}
		return listT;
	}

	/**
	 * The Class ManageOneToMany.
	 */
	private class ManageOneToMany {
		
		/** The select. */
		private String select;

		/** The one to many. */
		private boolean oneToMany;

		/**
		 * Instantiates a new manage one to many.
		 *
		 * @param select the select
		 * @param oneToMany the one to many
		 */
		public ManageOneToMany(String select, boolean oneToMany) {
			super();
			this.select = select;
			this.oneToMany = oneToMany;
		}

		/**
		 * Gets the select.
		 *
		 * @return the select
		 */
		public String getSelect() {
			return select;
		}

		/**
		 * Checks if is one to many.
		 *
		 * @return true, if is one to many
		 */
		public boolean isOneToMany() {
			return oneToMany;
		}


	}

}