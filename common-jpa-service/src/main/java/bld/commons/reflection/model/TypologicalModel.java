package bld.commons.reflection.model;

import javax.validation.constraints.NotNull;

public class TypologicalModel<ID> extends BasicModel<ID> {

	@NotNull
	private String name;

	public TypologicalModel() {
		super();
	}

	public TypologicalModel(ID id,@NotNull String name) {
		super(id);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "Typological [name=" + name + ", toString()=" + super.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		TypologicalModel<?> other = (TypologicalModel<?>) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	
	
	
	
}
