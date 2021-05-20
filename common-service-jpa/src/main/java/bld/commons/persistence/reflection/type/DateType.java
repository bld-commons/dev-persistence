/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.persistence.reflection.type.DateType.java
 */
package bld.commons.persistence.reflection.type;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

/**
 * The Enum DateType.
 */
public enum DateType {

	/** The date. */
	DATE(Date.class),
	
	/** The calendar. */
	CALENDAR(Calendar.class),
	
	/** The timestamp. */
	TIMESTAMP(Timestamp.class);
	
	
	
	/**
	 * Instantiates a new date type.
	 *
	 * @param type the type
	 */
	private DateType(Class<?> type) {
		this.type=type;
	}

	/** The type. */
	private Class<?> type;

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public Class<?> getType() {
		return type;
	}
	
	
	
	
}
