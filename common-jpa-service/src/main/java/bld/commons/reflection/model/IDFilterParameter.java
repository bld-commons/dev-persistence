package bld.commons.reflection.model;

import java.util.ArrayList;
import java.util.List;

public class IDFilterParameter<ID> extends FilterParameter {

	private List<ID> id;

	public IDFilterParameter() {
		super();
		this.id = new ArrayList<>();
	}

	public List<ID> getId() {
		return id;
	}

	public void setId(List<ID> id) {
		this.id = id;
	}

}
