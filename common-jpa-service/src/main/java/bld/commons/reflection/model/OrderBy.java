package bld.commons.reflection.model;

import javax.validation.constraints.NotNull;

import bld.commons.reflection.type.OrderType;

// TODO: Auto-generated Javadoc
/**
 * The Class OrderBy.
 */
public class OrderBy {

	/** The sort key. */
	@NotNull
	private String sortKey;

	/** The order type. */
	private OrderType orderType;

	/**
	 * Instantiates a new order by.
	 */
	public OrderBy() {
		super();
		orderType=OrderType.asc;
	}

	/**
	 * Instantiates a new order by.
	 *
	 * @param sortKey the sort key
	 * @param orderType the order type
	 */
	public OrderBy(String sortKey, OrderType orderType) {
		super();
		this.sortKey = sortKey;
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
