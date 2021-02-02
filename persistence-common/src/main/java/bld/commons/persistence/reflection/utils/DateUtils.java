/**************************************************************************
 * 
 * Copyright 2018 (C) DXC Technology
 * 
 * Author      : DXC Technology
 * Project Name: pmg-common
 * Package     : com.bld.pmg.utils
 * File Name   : DateUtils.java
 *
 ***************************************************************************/
package bld.commons.persistence.reflection.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import bld.commons.persistence.reflection.type.TimeUnitMeasureType;

public class DateUtils {

	private static final Log logger = LogFactory.getLog(DateUtils.class);



	public static TimeZone getTimezone(String timezone) {
		return TimeZone.getTimeZone(timezone);
	}

	public static Date calendarToDate(Calendar cal) {
		if (cal != null) {
			return cal.getTime();
		}
		return null;
	}

	public static Calendar dateToCalendar(Date data) {
		Calendar calendario = null;
		if (data != null) {
			calendario = Calendar.getInstance();
			calendario.setTimeInMillis(data.getTime());
		}
		return calendario;
	}

	public static Date currentDate() {
		Calendar calendario = Calendar.getInstance();
		return calendarToDate(resetHour(calendario));
	}

	public static Calendar stringToCalendarDate(String dateString,  String dateFormat) {
		Date date=stringToDate(dateString, dateFormat);
		Calendar calendar=Calendar.getInstance();
		calendar.setTime(date);
		return resetHour(calendar);
	}

	


	public static Date stringToDate(String createTimestamp, String dateFormat) {
		try {
			SimpleDateFormat sdf=new SimpleDateFormat(dateFormat);
			Date date = sdf.parse(createTimestamp);
			return date;
		} catch (Exception e) {
			logger.error("error while converting string to Date - input: '" + createTimestamp + "'", e);
		}
		return null;
	}

	

	public static String calendarToString(Calendar cal, String formato) {
		return dateToString(calendarToDate(cal), formato);
	}

	public static String dateToString(Date data, String dateFormat) {
		String dateToString = null;

		if (data != null && StringUtils.isNotBlank(dateFormat)) {
			try {
				SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
				dateToString = sdf.format(data.getTime());
			} catch (Exception e) {
				logger.error("error while converting calendar to string", e);
			}
		} else {
			logger.info("Null Parameter -> date:" + data + ", formato: " + dateFormat);
		}
		return dateToString;
	}




	public static Calendar today() {
		return resetHour(Calendar.getInstance());
	}



	
	public static Calendar resetHour(Calendar cal) {
		if (cal == null)
			return null;

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal;
	}


	public static Date resetHour(Date date) {
		if (date == null)
			return null;
		Calendar cal = dateToCalendar(date);
		cal = resetHour(cal);

		return calendarToDate(cal);
	}

	
	public static Long differneceDate(Calendar maxDate, Calendar minDate,TimeUnitMeasureType timeUnitMeasureType) {
		if (maxDate != null && minDate != null) {
			maxDate = resetHour(maxDate);
			minDate = resetHour(minDate);
			long giorni = (maxDate.getTime().getTime() - minDate.getTime().getTime()) / timeUnitMeasureType.getTime();
			return giorni;
		}
		return null;
	}

	public static Long differneceDate(Date maxDate, Date minDate,TimeUnitMeasureType timeUnitMeasureType) {
		if (maxDate != null && minDate != null) {
			return differneceDate(dateToCalendar(maxDate), dateToCalendar(minDate),timeUnitMeasureType);
		}
		return null;
	}

	/**
	 * Gets the current year.
	 *
	 * @return the current year
	 */
	public static int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}



	/**
	 * Sum date.
	 *
	 * @param date  the date
	 * @param day   the day
	 * @param month the month
	 * @param year  the year
	 * @return the date
	 */
	public static Date sumDate(Date date, int day, int month, int year) {
		Calendar calendar=sumDate(dateToCalendar(date), day, month, year);
		if(calendar!=null)
			date=calendar.getTime();
		return date;
	}

	/**
	 * Sum date.
	 *
	 * @param calendar the calendar
	 * @param day      the day
	 * @param month    the month
	 * @param year     the year
	 * @return the calendar
	 */
	public static Calendar sumDate(Calendar calendar, int day, int month, int year) {
		if (calendar != null) {
			calendar.add(Calendar.DATE, day);
			calendar.add(Calendar.MONTH, month);
			calendar.add(Calendar.YEAR, year);

		}
		return calendar;
	}
}
