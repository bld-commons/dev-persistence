package com.bld.commons.service;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.bld.commons.exception.JdbcTemplateServiceException;
import com.bld.commons.reflection.model.BuildNativeQueryParameter;
import com.bld.commons.reflection.model.ConditionsZoneModel;
import com.bld.commons.reflection.model.NativeQueryParameter;
import com.bld.commons.reflection.utils.ReflectionCommons;
import com.bld.commons.utils.CamelCaseUtils;

import jakarta.persistence.Query;

// TODO: Auto-generated Javadoc
/**
 * The Class JdbcTemplateServiceImpl.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
public abstract class JdbcTemplateServiceImpl<T, ID> extends JpaServiceImpl<T, ID> implements JdbcTemplateService<T, ID> {

	/**
	 * Gets the jdbc template.
	 *
	 * @return the jdbc template
	 */
	protected abstract NamedParameterJdbcTemplate getJdbcTemplate();

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(JdbcTemplateServiceImpl.class);

	/**
	 * Jdbc find by filter.
	 *
	 * @param <K>            the key type
	 * @param queryParameter the query parameter
	 * @param sql            the sql
	 * @return the list
	 */
	@Override
	public <K> List<K> jdbcFindByFilter(NativeQueryParameter<K, ID> queryParameter, String sql) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, sql);
		return this.jdbcFindByFilter(buildQueryFilter,null);
	}
	
	@Override
	public <K> List<K> jdbcFindByFilter(NativeQueryParameter<K, ID> queryParameter, String sql, RowMapper<K> mapper) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, sql);
		return this.jdbcFindByFilter(buildQueryFilter,mapper);
	}

	/**
	 * Jdbc single result by filter.
	 *
	 * @param <K>            the key type
	 * @param queryParameter the query parameter
	 * @param sql            the sql
	 * @return the k
	 */
	@Override
	public <K> K jdbcSingleResultByFilter(NativeQueryParameter<K, ID> queryParameter, String sql) {
		BuildNativeQueryParameter<K, ID> buildQueryFilter = getBuildNativeQueryFilter(queryParameter, sql);
		List<K> list = this.jdbcFindByFilter(buildQueryFilter,null);
		K k = null;
		if (list.size() > 1)
			throw new RuntimeException("Find multiple record");
		else if (list != null)
			k = list.get(0);
		return k;
	}

	/**
	 * Jdbc find by filter.
	 *
	 * @param <K>              the key type
	 * @param buildQueryFilter the build query filter
	 * @return the list
	 */
	private <K> List<K> jdbcFindByFilter(BuildNativeQueryParameter<K, ID> buildQueryFilter,RowMapper<K> mapper) {
		NativeQueryParameter<K, ID> queryParameter = buildQueryFilter.getQueryParameter();
		NamedParameterJdbcTemplate jdbcTemplate = setJdbcTemplate(queryParameter);
		StringBuilder sql = buildNativeQuery(buildQueryFilter);
		addOrderBy(buildQueryFilter.getQueryParameter().getListOrderBy(), sql, buildQueryFilter.getMapOrders());
		Map<String, Object> parameters = new HashMap<>();
		if (buildQueryFilter.getQueryParameter().getPageable() != null) {
			sql.append("\n").append("LIMIT :pageSize OFFSET (:pageSize*:pageNumber)");
			parameters.put("pageSize", queryParameter.getPageable().getPageSize());
			parameters.put("pageNumber", queryParameter.getPageable().getPageNumber());
		}

		final String select = sql.toString();
		logger.debug(select);
		parameters.putAll(parameters(queryParameter.getMapConditionsZone()));
		Map<String,Field>mapFields=ReflectionCommons.mapFields(buildQueryFilter.getQueryParameter().getResultClass());
		List<K> listK = new ArrayList<>();
		if(mapper==null) {
			extractedResult(buildQueryFilter, jdbcTemplate, parameters, select, mapFields, listK);
		}else {
			listK=jdbcTemplate.query(select,parameters,mapper);
		}
		
//		List<Map<String, Object>> result = jdbcTemplate.queryForList(select, parameters);
//		
//		for (Map<String, Object> item : result) {
//			K k = this.reflectionCommons.reflection(buildQueryFilter.getQueryParameter().getResultClass(), item);
//			listK.add(k);
//		}

		return listK;
	}


	private <K> void extractedResult(BuildNativeQueryParameter<K, ID> buildQueryFilter,
			NamedParameterJdbcTemplate jdbcTemplate, Map<String, Object> parameters, final String select,
			Map<String, Field> mapFields, List<K> listK) {
		jdbcTemplate.query(select,parameters, (ResultSet rs) -> {
			ResultSetMetaData metaData = rs.getMetaData();
			try {
				Map<String,Object>result=new HashMap<>();
				for (int i = 1; i <= metaData.getColumnCount(); i++) {
					String fieldName = CamelCaseUtils.camelCase(metaData.getColumnName(i), true);
					if(mapFields.containsKey(fieldName)) 
						result.put(metaData.getColumnName(i), rs.getObject(metaData.getColumnName(i)));
				}
				K k = this.reflectionCommons.reflection(buildQueryFilter.getQueryParameter().getResultClass(), result);
				listK.add(k);
			} catch (Exception e) {
				throw new JdbcTemplateServiceException(e);
			}
		});
	}

	/**
	 * Sets the jdbc template.
	 *
	 * @param queryParameter the query parameter
	 * @return the named parameter jdbc template
	 */
	private NamedParameterJdbcTemplate setJdbcTemplate(NativeQueryParameter<?, ID> queryParameter) {
		NamedParameterJdbcTemplate jdbcTemplate = this.getJdbcTemplate();
		if (queryParameter.getJdbcTemplate() != null)
			jdbcTemplate = queryParameter.getJdbcTemplate();
		return jdbcTemplate;
	}

	/**
	 * Parameters.
	 *
	 * @param <Q>     the generic type
	 * @param mapZone the map zone
	 * @return the map
	 */
	private <Q extends Query> Map<String, Object> parameters(Map<String, ConditionsZoneModel> mapZone) {
		Map<String, Object> parameters = new HashMap<>();
		for (ConditionsZoneModel zone : mapZone.values()) {
			for (Entry<String, Object> entry : zone.getParameters().entrySet()) {
				logger.debug("Parameter: " + entry.getKey() + "= " + entry.getValue());
				logger.debug("Class: " + (entry.getValue() != null ? entry.getValue().getClass().getName() : " null"));
				parameters.put(entry.getKey(), entry.getValue());
			}

		}
		return parameters;
	}
}
