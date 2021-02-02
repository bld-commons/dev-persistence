package bld.commons.persistence.reflection.type;

// TODO: Auto-generated Javadoc
/**
 * The Enum TimeUnitMeasureType.
 */
public enum TimeUnitMeasureType {

	/** The milliseconds. */
	milliseconds((long)1),
	
	/** The seconds. */
	seconds((long)1000),
	
	/** The minutes. */
	minutes((long)60000),
	
	/** The hours. */
	hours((long)3600000),
	
	/** The days. */
	days((long)86400000);
	
	
	/** The time. */
	private long time;

	
	
	/**
	 * Instantiates a new time unit measure type.
	 *
	 * @param time the time
	 */
	private TimeUnitMeasureType(long time) {
		this.time=time;
	}



	/**
	 * Gets the time.
	 *
	 * @return the time
	 */
	public long getTime() {
		return time;
	}
	
	
	
	
}
