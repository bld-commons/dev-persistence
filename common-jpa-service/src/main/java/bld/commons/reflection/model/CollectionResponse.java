package bld.commons.reflection.model;

import java.util.Collection;

public class CollectionResponse<T> extends ObjectResponse<Collection<T>> {

	/** The num results. */
	private Long totalCount;

	/** The page size. */
	private Integer pageSize;

	/** The page number. */
	private Integer pageNumber;

	public CollectionResponse() {
		super();
	}
		public CollectionResponse(Collection<T> data) {
		super(data);
	
	}

	public Long getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
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


}
