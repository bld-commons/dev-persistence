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

import bld.commons.reflection.model.BaseParameter;
import bld.commons.reflection.model.BuildJpqlQueryParameter;
import bld.commons.reflection.model.BuildNativeQueryParameter;
import bld.commons.reflection.model.OrderBy;
import bld.commons.reflection.model.QueryParameter;
import bld.commons.reflection.utils.ReflectionCommons;

/**
 * The Class BaseJpaService.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("unchecked")
public abstract class BaseJpaService<T,ID> {
	

	/** The classe. */
	private Class<T> clazz = null;


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
	public static final String WHERE_1_1 = " WHERE 1=1 ";

	/** The Constant WHERE. */
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

	/** The reflection commons. */
	@Autowired
	protected ReflectionCommons reflectionCommons;

	/**
	 * Instantiates a new base jpa service.
	 */
	public BaseJpaService() {
		super();
		this.clazz = ReflectionCommons.getGenericTypeClass(this);
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
	 * Gets the clazz.
	 *
	 * @return the clazz
	 */
	protected Class<T> getClazz() {
		return clazz;
	}


	/**
	 * Gets the where condition.
	 *
	 * @param mapParameters the map parameters
	 * @param select the select
	 * @param mapConditions the map conditions
	 * @param classFilterParameter the class filter parameter
	 * @return the where condition
	 */
	private String getWhereCondition(Map<String, Object> mapParameters, String select, Map<String, String> mapConditions, Class<? extends BaseParameter> classFilterParameter) {
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
	 * @param <Q> the generic type
	 * @param mapParameters the map parameters
	 * @param query the query
	 * @return the q
	 */
	private <Q extends Query> Q setQueryParameters(Map<String, Object> mapParameters, Q query) {
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
	 * @param buildQueryFilter the build query filter
	 */
	public void deleteByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		String delete = buildSql(buildQueryFilter);
		logger.debug("Query= " + delete);
		TypedQuery<?> query = (TypedQuery<?>) this.getEntityManager().createQuery(delete);
		query = setQueryParameters(queryFilter.getMapParameters(), query);
		query.executeUpdate();
	}

	/**
	 * Find by filter.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	public List<T> findByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		TypedQuery<T> query = buildQuery(buildQueryFilter);
		return query.getResultList();
	}

	/**
	 * Builds the query.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the typed query
	 */
	private  TypedQuery<T> buildQuery(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		Map<String, Object> mapParameters = queryFilter.getMapParameters();
		String select = buildSql(buildQueryFilter);
		ManageOneToMany manageOneToMany = addRelationshipsOneToMany(mapParameters, select, queryFilter.getNullables());
		select = manageOneToMany.getSelect();
		select = addOrderBy(queryFilter.getListOrderBy(), select);

		logger.info("\nQuery: \n" + select);
		TypedQuery<T> query = this.getEntityManager().createQuery(select, this.clazz);
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
	 * @param queryFilter the query filter
	 * @return the class filter parameter
	 */
	private  Class<? extends BaseParameter> getClassFilterParameter(QueryParameter<T, ID> queryFilter) {
		Class<? extends BaseParameter> classFilterParameter = null;
		if (queryFilter.getBaseParameter() != null)
			classFilterParameter = (Class<? extends BaseParameter>) queryFilter.getBaseParameter().getClass();
		return classFilterParameter;
	}

	/**
	 * Builds the sql.
	 *
	 * @param buildQueryFilter the build query filter
	 * @return the string
	 */
	private  String buildSql(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		String select = getWhereCondition(queryFilter.getNullables(), buildQueryFilter.getSql(), buildQueryFilter.getMapConditions());
		select = getWhereCondition(queryFilter.getMapParameters(), select, buildQueryFilter.getMapConditions(), getClassFilterParameter(queryFilter));
		return select;
	}


	/**
	 * Adds the order by.
	 *
	 * @param listOrderBy the list order by
	 * @param select the select
	 * @return the string
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
	 * @param mapParameters the map parameters
	 * @param select the select
	 * @param nullables the nullables
	 * @return the manage one to many
	 */
	private ManageOneToMany addRelationshipsOneToMany(Map<String, Object> mapParameters, String select, Set<String> nullables) {
		String innerJoin = " ";
		Set<String> listJoin = new HashSet<>();
		for (String key : this.mapOneToMany.keySet()) {
			if (mapParameters.containsKey(key) || (nullables != null && nullables.contains(key))) {
				Set<String> joins = mapOneToMany.get(key);
				for (String join : joins) {
					join = ReflectionCommons.removeExtraSpace(join);
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
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public Long countByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
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
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public T findSingleResultByFilter(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
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
	 * @param buildQueryFilter the build query filter
	 * @return the t
	 */
	public T findById(BuildJpqlQueryParameter<T, ID> buildQueryFilter) {
		QueryParameter<T, ID> queryFilter = buildQueryFilter.getQueryParameter();
		queryFilter.getMapParameters().put(QueryParameter.ID, queryFilter.getId());
		return findSingleResultByFilter(buildQueryFilter);
	}

	/**
	 * Native query select by filter.
	 *
	 * @param <K> the key type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	public <K> List<K> nativeQuerySelectByFilter(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		String sql = buildNativeQuery(buildQueryFilter);
		sql = addOrderBy(buildQueryFilter.getQueryParameter().getListOrderBy(), sql);
		logger.debug(sql);
		Query query = this.getEntityManager().createNativeQuery(sql, Tuple.class);
		query = setQueryParameters(buildQueryFilter.getQueryParameter().getMapParameters(), query);
		if (buildQueryFilter.getQueryParameter().getPageable() != null) {
			query.setFirstResult(buildQueryFilter.getQueryParameter().getPageable().getPageNumber() * buildQueryFilter.getQueryParameter().getPageable().getPageSize());
			query.setMaxResults(buildQueryFilter.getQueryParameter().getPageable().getPageSize());
		}

		List<Tuple> results = query.getResultList();
		List<K> listK = new ArrayList<>();
		for (Tuple row : results) {
			List<TupleElement<?>> elements = row.getElements();
			K k = null;
			try {
				k = buildQueryFilter.getQueryParameter().getResultClass().getConstructor().newInstance();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			Map<String, Object> mapRow = new HashMap<>();
			for (TupleElement<?> element : elements) {
				Object value = row.get(element.getAlias());
				if (value != null)
					mapRow.put(element.getAlias(), value);
			}
			this.reflectionCommons.reflection(k, mapRow);
			listK.add(k);
		}
		return listK;
	}

	
	
	/**
	 * Native query count by filter.
	 *
	 * @param <K> the key type
	 * @param buildQueryFilter the build query filter
	 * @return the long
	 */
	public <K> Long nativeQueryCountByFilter(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		String sql = buildNativeQuery(buildQueryFilter);
		logger.debug(sql);
		Query query = this.getEntityManager().createNativeQuery(sql);
		query = setQueryParameters(buildQueryFilter.getQueryParameter().getMapParameters(), query);
		Number count = (Number) query.getSingleResult();
		return count.longValue();
	}

	/**
	 * Builds the native query.
	 *
	 * @param <K> the key type
	 * @param buildQueryFilter the build query filter
	 * @return the string
	 */
	private <K> String buildNativeQuery(BuildNativeQueryParameter<K, ID> buildQueryFilter) {
		String where = WHERE_1_1;
		String sql = buildQueryFilter.getSql();
		for (String key : buildQueryFilter.getQueryParameter().getMapParameters().keySet())
			if (buildQueryFilter.getMapConditions().containsKey(key))
				where += buildQueryFilter.getMapConditions().get(key);

		for (String nullable : buildQueryFilter.getQueryParameter().getNullables())
			if (buildQueryFilter.getMapConditions().containsKey(nullable))
				where += buildQueryFilter.getMapConditions().get(nullable);

		sql = sql.replace(WHERE, where);
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