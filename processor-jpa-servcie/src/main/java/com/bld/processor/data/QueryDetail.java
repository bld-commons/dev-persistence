package com.bld.processor.data;

public class QueryDetail {

	private String alias;

	private String key;

	private boolean nullable;
	
	private boolean many;
	
	private ClassField classField;
	

	public QueryDetail() {
		super();
	}



	public QueryDetail(String alias, String key, boolean nullable, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.nullable = nullable;
		this.classField = classField;
		this.many=false;
				
	}

	

	public QueryDetail(String alias, String key, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.classField = classField;
		this.many=false;
		this.nullable=false;
	}



	public QueryDetail(String alias, String key, boolean nullable, boolean many, ClassField classField) {
		super();
		this.alias = alias;
		this.key = key;
		this.nullable = nullable;
		this.many = many;
		this.classField = classField;
	}


	public String getAlias() {
		return alias;
	}


	public void setAlias(String alias) {
		this.alias = alias;
	}


	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}


	public boolean isNullable() {
		return nullable;
	}


	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	

	public boolean isMany() {
		return many;
	}


	public void setMany(boolean many) {
		this.many = many;
	}


	public ClassField getClassField() {
		return classField;
	}


	public void setClassField(ClassField classFieldRefernce) {
		this.classField = classFieldRefernce;
	}

	
}
