package bld.commons.persistence.reflection.type;

public enum TimeUnitMeasureType {

	milliseconds((long)1),
	seconds((long)1000),
	minutes((long)60000),
	hours((long)3600000),
	days((long)86400000);
	
	
	private long time;

	
	
	private TimeUnitMeasureType(long time) {
		this.time=time;
	}



	public long getTime() {
		return time;
	}
	
	
	
	
}
