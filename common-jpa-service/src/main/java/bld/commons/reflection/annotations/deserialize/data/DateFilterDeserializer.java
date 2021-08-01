package bld.commons.reflection.annotations.deserialize.data;

public class DateFilterDeserializer {

	private String timeZone;

	private String format;

	private boolean equals;

	private int addYear;

	private int addMonth;

	private int addWeek;

	private int addDay;

	private int addHour;

	private int addMinute;

	private int addSecond;

	public DateFilterDeserializer(String timeZone, String format) {
		super();
		this.timeZone = timeZone;
		this.format = format;
		this.equals = true;
		this.addDay = 0;
		this.addHour = 0;
		this.addMinute = 0;
		this.addMonth = 0;
		this.addSecond = 0;
		this.addWeek = 0;
		this.addYear = 0;
	}

	public DateFilterDeserializer(String timeZone, String format, boolean equals, int addYear, int addMonth, int addWeek, int addDay, int addHour, int addMinute, int addSecond) {
		super();
		this.timeZone = timeZone;
		this.format = format;
		this.equals = equals;
		this.addYear = addYear;
		this.addMonth = addMonth;
		this.addWeek = addWeek;
		this.addDay = addDay;
		this.addHour = addHour;
		this.addMinute = addMinute;
		this.addSecond = addSecond;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public String getFormat() {
		return format;
	}

	public boolean isEquals() {
		return equals;
	}

	public int getAddYear() {
		return addYear;
	}

	public int getAddMonth() {
		return addMonth;
	}

	public int getAddWeek() {
		return addWeek;
	}

	public int getAddDay() {
		return addDay;
	}

	public int getAddHour() {
		return addHour;
	}

	public int getAddMinute() {
		return addMinute;
	}

	public int getAddSecond() {
		return addSecond;
	}

	

	
}
