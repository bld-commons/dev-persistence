package bld.commons.reflection.model;

import javax.validation.constraints.NotNull;

public class BasicModel<ID> {
	
	private ID id;
	
	@NotNull
	private String name;
	
	

	public BasicModel() {
		super();
	}
	
	

	public BasicModel(ID id, @NotNull String name) {
		super();
		this.id = id;
		this.name = name;
	}



	public ID getId() {
		return id;
	}

	public void setId(ID idModel) {
		this.id = idModel;
	}

	public String getName() {
		return name;
	}

	public void setName(String desModel) {
		this.name = desModel;
	}

	@Override
	public String toString() {
		return "BasicModel [id=" + id + ", name=" + name + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		BasicModel<?> other = (BasicModel<?>) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}
	
	
	
	
}
