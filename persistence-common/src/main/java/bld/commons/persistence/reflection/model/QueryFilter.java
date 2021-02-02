package bld.commons.persistence.reflection.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class QueryFilter <T, ID>{
	
	public final static String ID = "id";

	private ID id;

	private Set<String> checkNullable;
	
	private Map<String, Object> mapParameters;

	private String sortKey;

	private String sortOrder;

	private Pageable pageable;

	private Class<T> classFilter;
	
	

	public QueryFilter() {
		super();
		this.checkNullable=new HashSet<>();
		this.mapParameters=new HashMap<>();
	}
	
	
	public QueryFilter(ID id) {
		this.id = id;
	}

	public QueryFilter(BaseFilterRequest filter) {
		super();
		this.checkNullable = new HashSet<>();
		this.mapParameters=new HashMap<>();
		if (filter != null) {
			this.sortKey = filter.getSortKey();
			this.sortOrder = filter.getSortOrder();
			if (filter.getPageNumber() != null && filter.getPageSize() != null)
				this.pageable = PageRequest.of(filter.getPageNumber(), filter.getPageSize());
		}

	}

	public QueryFilter(Integer page, Integer size, Map<String, Object> mapParametri, Set<String> checkNullable) {
		super();
		this.checkNullable = checkNullable;
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
	}

	public QueryFilter(Integer page, Integer size, Map<String, Object> mapParametri) {
		super();
		this.checkNullable = new HashSet<>();
		if (page != null && size != null)
			this.pageable = PageRequest.of(page, size);
	}
	

	public ID getId() {
		return id;
	}

	public void setId(ID id) {
		this.id = id;
	}

	public Set<String> getCheckNullable() {
		return checkNullable;
	}

	public void setCheckNullable(Set<String> checkNullable) {
		this.checkNullable = checkNullable;
	}

	public Map<String, Object> getMapParameters() {
		return mapParameters;
	}

	public void setMapParameters(Map<String, Object> mapParameters) {
		this.mapParameters = mapParameters;
	}

	public String getSortKey() {
		return sortKey;
	}

	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public void setSortOrder(String sortOrder) {
		this.sortOrder = sortOrder;
	}

	public Pageable getPageable() {
		return pageable;
	}

	public void setPageable(Pageable pageable) {
		this.pageable = pageable;
	}

	public Class<T> getClassFilter() {
		return classFilter;
	}

	public void setClassFilter(Class<T> classFilter) {
		this.classFilter = classFilter;
	}
	
	
	
	
	
	
	
	
}
