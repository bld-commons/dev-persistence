/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class com.bld.processor.data.QueryDetail.java
 */
package com.bld.processor.data;

/**
 * The Class QueryDetail.
 */
public class QueryDetail {

	/** The alias. */
	private String alias;

	/** The key. */
	private String key;

	/** The nullable. */
	private boolean nullable;
	
	/** The many. */
	private boolean many;
	
	/** The class field. */
	private ClassField classField;
	

	/**
	 * Instantiates a new query detail.
	 */
	public QueryDetail() {
		super();
	}



	/**
	 * Instantiates a new query detail.
	 *
	 * @param alias the alias
	 * @param key the key
	 * @param nullable the nullable
	 * @param classField the class field
	 */
	public QueryDetail(String alias, String key, boolean nullable, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.nullable = nullable;
		this.classField = classField;
		this.many=false;
				
	}

	

	/**
	 * Instantiates a new query detail.
	 *
	 * @param alias the alias
	 * @param key the key
	 * @param classField the class field
	 */
	public QueryDetail(String alias, String key, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.classField = classField;
		this.many=false;
		this.nullable=false;
	}



	/**
	 * Instantiates a new query detail.
	 *
	 * @param alias the alias
	 * @param key the key
	 * @param nullable the nullable
	 * @param many the many
	 * @param classField the class field
	 */
	public QueryDetail(String alias, String key, boolean nullable, boolean many, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.nullable = nullable;
		this.many = many;
		this.classField = classField;
	}


	/**
	 * Gets the alias.
	 *
	 * @return the alias
	 */
	public String getAlias() {
		return alias;
	}


	/**
	 * Sets the alias.
	 *
	 * @param alias the new alias
	 */
	public void setAlias(String alias) {
		this.alias = alias;
	}


	/**
	 * Gets the key.
	 *
	 * @return the key
	 */
	public String getKey() {
		return key;
	}


	/**
	 * Sets the key.
	 *
	 * @param key the new key
	 */
	public void setKey(String key) {
		this.key = key;
	}


	/**
	 * Checks if is nullable.
	 *
	 * @return true, if is nullable
	 */
	public boolean isNullable() {
		return nullable;
	}


	/**
	 * Sets the nullable.
	 *
	 * @param nullable the new nullable
	 */
	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	

	/**
	 * Checks if is many.
	 *
	 * @return true, if is many
	 */
	public boolean isMany() {
		return many;
	}


	/**
	 * Sets the many.
	 *
	 * @param many the new many
	 */
	public void setMany(boolean many) {
		this.many = many;
	}


	/**
	 * Gets the class field.
	 *
	 * @return the class field
	 */
	public ClassField getClassField() {
		return classField;
	}


	/**
	 * Sets the class field.
	 *
	 * @param classFieldRefernce the new class field
	 */
	public void setClassField(ClassField classFieldRefernce) {
		this.classField = classFieldRefernce;
	}

	
}
