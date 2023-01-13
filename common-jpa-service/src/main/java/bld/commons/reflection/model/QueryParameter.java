/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.model.QueryFilter.java
 */
package bld.commons.reflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

/**
 * The Class QueryFilter.
 *
 * @param <T>  the generic type
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class QueryParameter<T, ID> extends BaseQueryParameter<T, ID> {

	/** The map parameters. */
	private Map<String, Object> parameters;

	/** The nullables. */
	private Set<String> nullables;

	
	
	/**
	 * Instantiates a new query parameter.
	 */
	public QueryParameter() {
		super();
		init();
	}

	/**
	 * Instantiates a new query parameter.
	 *
	 * @param baseParameter the base parameter
	 */
	public QueryParameter(BaseParameter baseParameter) {
		super(baseParameter);
		init();
	}

	/**
	 * Inits the.
	 */
	protected void init() {
		super.init();
		this.nullables = new HashSet<>();
		this.parameters = new HashMap<>();
	}

	/**
	 * Instantiates a new query parameter.
	 *
	 * @param id the id
	 */
	public QueryParameter(ID id) {
		super(id);
		init();
		this.parameters.put(ID, id);
	}

	/**
	 * Gets the check nullable.
	 *
	 * @return the check nullable
	 */
	public Set<String> getNullables() {
		return nullables;
	}

	/**
	 * Adds the parameter.
	 *
	 * @param key   the key
	 * @param value the value
	 */
	public void addParameter(String key, Object value) {
		if (key != null && value != null)
			this.parameters.put(key, value);
	}

	/**
	 * Adds the nullable.
	 *
	 * @param nullable the nullable
	 */
	public void addNullable(String nullable) {
		if (StringUtils.isNotBlank(nullable))
			this.nullables.add(nullable);
	}

	/**
	 * Gets the parameters.
	 *
	 * @return the parameters
	 */
	public Map<String, Object> getParameters() {
		return parameters;
	}

}
