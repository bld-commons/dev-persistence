/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.processor.OperationType.java
 */
package com.bld.commons.processor;

import com.bld.commons.service.BaseJpaService;

/**
 * The Enum OperationType.
 */
public enum OperationType {

	
	
	/** The in. */
	IN(" in ("+BaseJpaService.KEY_PROPERTY+") "),
	
	/** The not in. */
	NOT_IN(" not in ("+BaseJpaService.KEY_PROPERTY+") "),
	
	/** The equal. */
	EQUAL(" = "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The not equal. */
	NOT_EQUAL(" <> "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The like. */
	LIKE(" like "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The greater. */
	GREATER(" > "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The greater equal. */
	GREATER_EQUAL(" >= "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The less. */
	LESS(" < "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The less equal. */
	LESS_EQUAL(" <= "+BaseJpaService.KEY_PROPERTY+" "),
	
	/** The is null. */
	IS_NULL(" is null "),
	
	/** The is not null. */
	IS_NOT_NULL(" is not null "),
	;
	
	
	
	/** The value. */
	private String value;

	/**
	 * Instantiates a new operation type.
	 *
	 * @param value the value
	 */
	private OperationType(String value) {
		this.value = value;
	}

	/**
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}
	
	
	
	
}


