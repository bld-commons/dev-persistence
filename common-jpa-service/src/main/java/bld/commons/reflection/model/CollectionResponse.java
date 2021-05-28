package bld.commons.reflection.model;

import java.util.Collection;

public class CollectionResponse<T> {

	private Collection<T> list;

	/** The num results. */
	private Long resultNumber;

	/** The page size. */
	private Integer pageSize;

	/** The page number. */
	private Integer pageNumber;

	public CollectionResponse() {
		super();
	}

	public Long getResultNumber() {
		return resultNumber;
	}

	public void setResultNumber(Long resultNumber) {
		this.resultNumber = resultNumber;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}

	public Collection<T> getList() {
		return list;
	}

	public void setList(Collection<T> list) {
		this.list = list;
	}

}
