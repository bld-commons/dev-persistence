package bld.commons.persistence.conditions;

public interface BaseConditions <E extends Enum<E>> {

	public String getCondition();
	
	public String name();
	
}
