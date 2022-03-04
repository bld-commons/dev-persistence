/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.CollectionResponse.java
 */
package bld.commons.reflection.model;

import java.util.Collection;

/**
 * The Class CollectionResponse.
 *
 * @param <T> the generic type
 */
@SuppressWarnings("serial")
public class CollectionResponse<T> extends ObjectResponse<Collection<T>> {

	/** The num results. */
	private Long totalCount;

	/** The page size. */
	private Integer pageSize;

	/** The page number. */
	private Integer pageNumber;

	/**
	 * Instantiates a new collection response.
	 */
	public CollectionResponse() {
		super();
	}
		
		/**
		 * Instantiates a new collection response.
		 *
		 * @param data the data
		 */
		public CollectionResponse(Collection<T> data) {
		super(data);
	
	}

	/**
	 * Gets the total count.
	 *
	 * @return the total count
	 */
	public Long getTotalCount() {
		return totalCount;
	}

	/**
	 * Sets the total count.
	 *
	 * @param totalCount the new total count
	 */
	public void setTotalCount(Long totalCount) {
		this.totalCount = totalCount;
	}

	/**
	 * Gets the page size.
	 *
	 * @return the page size
	 */
	public Integer getPageSize() {
		return pageSize;
	}

	/**
	 * Sets the page size.
	 *
	 * @param pageSize the new page size
	 */
	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	/**
	 * Gets the page number.
	 *
	 * @return the page number
	 */
	public Integer getPageNumber() {
		return pageNumber;
	}

	/**
	 * Sets the page number.
	 *
	 * @param pageNumber the new page number
	 */
	public void setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
	}


}
