package bld.commons.processor;

import bld.commons.service.BaseJpaService;

public enum OperationType {

	
	
	IN(" in ("+BaseJpaService.KEY_PROPERTY+") "),
	NOT_IN(" not in ("+BaseJpaService.KEY_PROPERTY+") "),
	EQUAL(" = "+BaseJpaService.KEY_PROPERTY+" "),
	NOT_EQUAL(" <> "+BaseJpaService.KEY_PROPERTY+" "),
	LIKE(" like "+BaseJpaService.KEY_PROPERTY+" "),
	GREATER(" > "+BaseJpaService.KEY_PROPERTY+" "),
	GREATER_EQUAL(" >= "+BaseJpaService.KEY_PROPERTY+" "),
	LESS(" < "+BaseJpaService.KEY_PROPERTY+" "),
	LESS_EQUAL(" <= "+BaseJpaService.KEY_PROPERTY+" "),
	IS_NULL(" is null "),
	IS_NOT_NULL(" is not null "),
	;
	
	
	
	private String value;

	private OperationType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
	
	
	
	
}


