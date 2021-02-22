package bld.commons.persistence.reflection.type;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

public enum DateType {

	DATE(Date.class),
	CALENDAR(Calendar.class),
	TIMESTAMP(Timestamp.class);
	
	
	
	private DateType(Class<?> type) {
		this.type=type;
	}

	private Class<?> type;

	public Class<?> getType() {
		return type;
	}
	
	
	
	
}
