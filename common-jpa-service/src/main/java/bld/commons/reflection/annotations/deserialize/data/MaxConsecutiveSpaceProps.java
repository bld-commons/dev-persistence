/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.deserialize.data.MaxConsecutiveSpaceProps.java
 */
package bld.commons.reflection.annotations.deserialize.data;

/**
 * The Class MaxConsecutiveSpaceProps.
 */
public class MaxConsecutiveSpaceProps {
	
	/** The consecutive. */
	private int consecutive;

	/** The trim. */
	private boolean trim;
	
	/** The remove endline. */
	private boolean removeEndline;
	
	/** The remove all space type. */
	private boolean removeAllSpaceType;



	/**
	 * Instantiates a new max consecutive space props.
	 *
	 * @param consecutive the consecutive
	 * @param trim the trim
	 * @param removeEndline the remove endline
	 * @param removeAllSpaceType the remove all space type
	 */
	public MaxConsecutiveSpaceProps(int consecutive, boolean trim, boolean removeEndline, boolean removeAllSpaceType) {
		super();
		this.consecutive = consecutive;
		this.trim = trim;
		this.removeEndline = removeEndline;
		this.removeAllSpaceType = removeAllSpaceType;
	}

	/**
	 * Gets the consecutive.
	 *
	 * @return the consecutive
	 */
	public int getConsecutive() {
		return consecutive;
	}

	/**
	 * Checks if is trim.
	 *
	 * @return true, if is trim
	 */
	public boolean isTrim() {
		return trim;
	}

	/**
	 * Checks if is removes the endline.
	 *
	 * @return true, if is removes the endline
	 */
	public boolean isRemoveEndline() {
		return removeEndline;
	}

	/**
	 * Checks if is removes the all space type.
	 *
	 * @return true, if is removes the all space type
	 */
	public boolean isRemoveAllSpaceType() {
		return removeAllSpaceType;
	}

	
}
