/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.base.service.BaseJpaService.java
 */
package bld.commons.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.Tuple;
import javax.persistence.TupleElement;
import javax.persistence.TypedQuery;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.jpa.QueryHints;
import org.springframework.beans.factory.annotation.Autowired;

import bld.commons.reflection.model.BuildQueryFilter;
import bld.commons.reflection.model.FilterParameter;
import bld.commons.reflection.model.OrderBy;
import bld.commons.reflection.model.QueryFilter;
import bld.commons.reflection.utils.ReflectionUtils;

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

	/** The Constant KEY_PROPERTY. */
	public final static String KEY_PROPERTY = "<PROPERTY>";

	public static final String WHERE_1_1 = " WHERE 1=1 ";

	public static final String WHERE = "<WHERE>";

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
	 * @param nullables     the nullables
	 * @param select        the select
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
	 * @param <T>              the generic type
	 * @param <ID>             the generic type
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
		String count = buildQueryFilter.getSql();
		ManageOneToMany manageOneToMany = addRelationshipsOneToMany(queryFilter.getMapParameters(), count, queryFilter.getNullables());
		count = manageOneToMany.getSelect();
		buildQueryFilter.setSql(count);
		count = buildSql(buildQueryFilter).replaceAll(fetch, "");
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

	public <T, ID> List<T> nativeQuerySelectByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		String sql = buildNativeQuery(buildQueryFilter);
		sql = addOrderBy(buildQueryFilter.getQueryFilter().getListOrderBy(), sql);

		Query query = this.getEntityManager().createNativeQuery(sql, Tuple.class);
		query = setQueryParameters(buildQueryFilter.getQueryFilter().getMapParameters(), query);
		if (buildQueryFilter.getQueryFilter().getPageable() != null) {
			query.setFirstResult(buildQueryFilter.getQueryFilter().getPageable().getPageNumber() * buildQueryFilter.getQueryFilter().getPageable().getPageSize());
			query.setMaxResults(buildQueryFilter.getQueryFilter().getPageable().getPageSize());
		}

		List<Tuple> results = query.getResultList();
		List<T> listT = new ArrayList<T>();
		for (Tuple row : results) {
			List<TupleElement<?>> elements = row.getElements();
			T t = null;
			try {
				t = buildQueryFilter.getQueryFilter().getResultClass().getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Map<String, Object> mapRow = new HashMap<>();
			for (TupleElement<?> element : elements) {
				Object value = row.get(element.getAlias());
				if (value != null)
					mapRow.put(element.getAlias(), value);
			}
			this.reflectionUtils.reflection(t, mapRow);
			listT.add(t);
		}
		return listT;
	}

	public <T, ID> Long nativeQueryCountByFilter(BuildQueryFilter<T, ID> buildQueryFilter) {
		String sql = buildNativeQuery(buildQueryFilter);
		sql = addOrderBy(buildQueryFilter.getQueryFilter().getListOrderBy(), sql);

		Query query = this.getEntityManager().createNativeQuery(sql, Long.class);
		query = setQueryParameters(buildQueryFilter.getQueryFilter().getMapParameters(), query);
		Long count = (Long) query.getSingleResult();
		return count;
	}

	private <T, ID> String buildNativeQuery(BuildQueryFilter<T, ID> buildQueryFilter) {
		String where = WHERE_1_1;
		String sql = buildQueryFilter.getSql();
		for (String key : buildQueryFilter.getQueryFilter().getMapParameters().keySet())
			if (buildQueryFilter.getMapConditions().containsKey(key))
				where += buildQueryFilter.getMapConditions().get(key);

		for (String nullable : buildQueryFilter.getQueryFilter().getNullables())
			if (buildQueryFilter.getMapConditions().containsKey(nullable))
				where += buildQueryFilter.getMapConditions().get(nullable);

		sql = sql.replace("(?i)" + WHERE, where);
		return sql;
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