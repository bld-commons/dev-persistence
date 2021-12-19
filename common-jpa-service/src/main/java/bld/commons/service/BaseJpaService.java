/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.BaseJpaService.java
 */
package bld.commons.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.zaxxer.hikari.HikariConfig;

import bld.commons.reflection.model.BuildQueryFilter;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.OrderBy;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.reflection.type.DriverClassPageble;
import bld.commons.reflection.utils.ReflectionUtils;

/**
 * The Class BaseJpaService.
 */
public abstract class BaseJpaService {

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

	/** The map one to many. */
	private Map<String, LinkedHashSet<String>> mapOneToMany;

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
	 * @param mapParameters        the map parameters
	 * @param select               the select
	 * @param mapConditions        the map conditions
	 * @param classFilterParameter the class filter parameter
	 * @return the where condition
	 */
	private String getWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions, Class<? extends FilterParameter> classFilterParameter) {
		for (String key : mapParameters.keySet()) {
			String val = mapConditions.get(key);
			logger.debug("Key: " + key + " Parameter: " + val);
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
			logger.debug("----------------------------------------------------------");
			logger.debug("Key: " + key);
			logger.debug("Value: " + value);
			logger.debug("Class: " + value.getClass().getName());
			query.setParameter(key, mapParameters.get(key));
		}

		return query;
	}

	/**
	 * Gets the where condition.
	 *
	 * @param nullables the nullables
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @return the where condition
	 */
	private String getWhereCondition(Set<String> nullables, String select, Map<String, String> mapConditions) {
		if (nullables != null) {
			for (String key : nullables) {
				logger.debug("String check nullable: " + key);
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
		String delete = buildSql(buildQueryFilter);
		logger.debug("Query= " + delete);
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
		String select = buildSql(buildQueryFilter);
		ManageOneToMany manageOneToMany = addRelationshipsOneToMany(mapParameters, select, queryFilter.getNullables());
		select = manageOneToMany.getSelect();
		select = addOrderBy(queryFilter.getListOrderBy(), select);

		logger.info("\nQuery: \n" + select);
		TypedQuery<T> query = this.getEntityManager().createQuery(select, queryFilter.getResultClass());
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
	 * @param <T>         the generic type
	 * @param <ID>        the generic type
	 * @param queryFilter the query filter
	 * @return the class filter parameter
	 */
	private <T, ID> Class<? extends FilterParameter> getClassFilterParameter(QueryFilter<T, ID> queryFilter) {
		Class<? extends FilterParameter> classFilterParameter = null;
		if (queryFilter.getFilterParameter() != null)
			classFilterParameter = (Class<? extends FilterParameter>) queryFilter.getFilterParameter().getClass();
		return classFilterParameter;
	}

	/**
	 * Builds the sql.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the string
	 */
	private <T, ID> String buildSql(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String select = getWhereCondition(queryFilter.getNullables(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions());
		select = getWhereCondition(queryFilter.getMapParameters(), select, buildQueryFilter.getMapConditions(), getClassFilterParameter(queryFilter));
		return select;
	}

//	private <T,ID>String buildQuery(BuildQueryFilter<T, ID> buildQueryFilter){
//	//private String buildQuery(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions, Set<String> checkNullable, Class<? extends FilterParameter> classFilterParameter) {
//		select = getWhereConditionNullOrNotNull(queryFilter.getNullables(), , mapConditions);
//		select = getWhereCondition(mapParameters, select, mapConditions, classFilterParameter);
//		return select;
//	}

	/**
	 * Gets the order by.
	 *
	 * @param listOrderBy the list order by
	 * @param select      the select
	 * @return the order by
	 */
	private String addOrderBy(List<OrderBy> listOrderBy, String select) {
		String writeOrderBy = "";
		if (CollectionUtils.isNotEmpty(listOrderBy)) {
			for (OrderBy orderBy : listOrderBy)
				writeOrderBy += "," + orderBy.getSortKey() + " " + orderBy.getOrderType().name();
			writeOrderBy = ORDER_BY + writeOrderBy.substring(1);
		}

		if (!select.toLowerCase().contains(ORDER_BY.trim()))
			select += writeOrderBy;
		return select;
	}

	/**
	 * Adds the relationships one to many.
	 *
	 * @param mapParameters the map parametri
	 * @param select        the select
	 * @param nullables     the check nullable
	 * @return the string
	 */
	private ManageOneToMany addRelationshipsOneToMany(Map<String, Object> mapParameters, String select, Set<String> nullables) {
		String innerJoin = " ";
		Set<String> listJoin = new HashSet<>();
		for (String key : this.mapOneToMany.keySet()) {
			if (mapParameters.containsKey(key) || (nullables != null && nullables.contains(key))) {
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
		ManageOneToMany manageOneToMany = addRelationshipsOneToMany(queryFilter.getMapParameters(), count, queryFilter.getNullables());
		count = manageOneToMany.getSelect();
		buildQueryFilter.setSql(count);
		count = buildSql(buildQueryFilter);
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
		String select = buildNativeSql(buildQueryFilter);
		select=getNativeOrderBy(queryFilter, select);
		select = addOrderBy(queryFilter.getListOrderBy(), select);

		buildQueryFilter.setSql(select);
		return this.jdbcSelect(buildQueryFilter);
	}

	/**
	 * Jdbc count native query.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 * @throws Exception the exception
	 */
	public <T, ID> Long jdbcCountNativeQuery(BuildQueryFilter<T, ID> buildQueryFilter) throws Exception {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String count = buildNativeSql(buildQueryFilter);
		return this.getJdbcTemplate().queryForObject(count, queryFilter.getMapParameters(), Long.class);
	}

	/**
	 * Builds the native sql.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param buildQueryFilter the build query filter
	 * @return the string
	 */
	private <T, ID> String buildNativeSql(BuildQueryFilter<T, ID> buildQueryFilter) {
		QueryFilter<T, ID> queryFilter = buildQueryFilter.getQueryFilter();
		String select = getWhereCondition(queryFilter.getNullables(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions());
		select = getNativeWhereCondition(queryFilter.getMapParameters(), select, buildQueryFilter.getMapConditions(), getClassFilterParameter(queryFilter));
		return select;
	}

	/**
	 * Gets the native where condition.
	 *
	 * @param mapParameters the map parameters
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @param classFilterParameter the class filter parameter
	 * @return the native where condition
	 */
	private String getNativeWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions, Class<? extends FilterParameter> classFilterParameter) {
		for (String key : mapParameters.keySet()) {
			if (mapConditions.containsKey(key)) {
				String val = mapConditions.get(key);
				logger.debug("Key: " + key + " Parameter: " + val);
				select += val;
			}
		}
		return select;
	}

	/**
	 * Gets the native order by.
	 *
	 * @param <T> the generic type
	 * @param <ID> the generic type
	 * @param queryFilter the query filter
	 * @param select the select
	 * @return the native order by
	 */
	private <T, ID> String getNativeOrderBy(QueryFilter<T, ID> queryFilter, String select) {
		if (CollectionUtils.isNotEmpty(queryFilter.getListOrderBy())) {
			select += " ORDER BY ";
			for (OrderBy orderBy : queryFilter.getListOrderBy())
				select += orderBy.getSortKey() + " " + orderBy.getOrderType().name()+"\n";

		}
		return select;
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
		if (this.getJdbcTemplate().getJdbcTemplate().getDataSource() instanceof HikariConfig && queryFilter.getPageable() != null) {
			HikariConfig hikariConfig = (HikariConfig) this.getJdbcTemplate().getJdbcTemplate().getDataSource();
			DriverClassPageble driverClassPageble = DriverClassPageble.valueOf(hikariConfig.getDriverClassName().replace(".", "_"));
			select += driverClassPageble.getPageable();
			int queryPageSize = queryFilter.getPageable().getPageSize();
			int queryPageNumber = queryFilter.getPageable().getPageNumber();
			queryFilter.getMapParameters().put("queryPageSize", Integer.valueOf(queryPageSize));
			queryFilter.getMapParameters().put("queryStartRow", Integer.valueOf(queryPageNumber * queryPageSize));
		}
		logger.info("\nQuery: \n" + select);
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource(queryFilter.getMapParameters());
		List<Map<String, Object>> listResult = this.getJdbcTemplate().queryForList(select, mapSqlParameterSource);
		List<T> listT = new ArrayList<T>();
		for (Map<String, Object> mapResult : listResult) {
			T t = queryFilter.getResultClass().getConstructor().newInstance();
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
		 * @param select    the select
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