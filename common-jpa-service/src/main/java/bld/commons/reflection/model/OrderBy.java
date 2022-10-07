/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.model.OrderBy.java
 */
package bld.commons.reflection.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import bld.commons.json.annotations.MaxConsecutiveSpace;
import bld.commons.reflection.type.OrderType;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderBy.
 */
@SuppressWarnings("serial")
public class OrderBy implements Serializable {

	/** The sort key. */
	@NotNull
	@MaxConsecutiveSpace(removeAllSpaceType = true)
	private String sortKey;

	/** The order type. */
	private OrderType orderType;

	/**
	 * Instantiates a new order by.
	 */
	public OrderBy() {
		super();
		orderType = OrderType.asc;
	}

	/**
	 * Instantiates a new order by.
	 *
	 * @param sortKey   the sort key
	 * @param orderType the order type
	 */
	public OrderBy(String sortKey, OrderType orderType) {
		super();
		this.sortKey = sortKey;
		this.orderType = OrderType.asc;
		if (orderType != null)
			this.orderType = orderType;
	}

	/**
	 * Gets the sort key.
	 *
	 * @return the sort key
	 */
	public String getSortKey() {
		return sortKey;
	}

	/**
	 * Sets the sort key.
	 *
	 * @param sortKey the new sort key
	 */
	public void setSortKey(String sortKey) {
		this.sortKey = sortKey;
	}

	/**
	 * Gets the order type.
	 *
	 * @return the order type
	 */
	public OrderType getOrderType() {
		return orderType;
	}

	/**
	 * Sets the order type.
	 *
	 * @param orderType the new order type
	 */
	public void setOrderType(OrderType orderType) {
		this.orderType = orderType;
	}

}
