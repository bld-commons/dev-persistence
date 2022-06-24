/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.IDFilterParameter.java
 */
package bld.commons.reflection.model;

import java.util.ArrayList;
import java.util.List;

/**
 * The Class IDFilterParameter.
 *
 * @param <ID> the generic type
 */
@SuppressWarnings("serial")
public class IDFilterParameter<ID> extends FilterParameter {

	/** The id. */
	private List<ID> id;

	/**
	 * Instantiates a new ID filter parameter.
	 */
	public IDFilterParameter() {
		super();	
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public List<ID> getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(List<ID> id) {
		this.id = id;
	}
	
	
	public void addId(ID id) {
		if(this.id==null)
			this.id=new ArrayList<>();
		this.id.add(id);
	}

}
