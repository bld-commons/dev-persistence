package bld.commons.reflection.model;

public class BaseModel<ID> {

	private ID id;

	public BaseModel() {
		super();
	}

	public BaseModel(ID id) {
		super();
		this.id = id;
	}

	public ID getId() {
		return id;
	}

	public void setId(ID idModel) {
		this.id = idModel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseModel<?> other = (BaseModel<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "BasicModel [id=" + id + "]";
	}
	
	

}
