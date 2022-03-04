/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.deserialize.CustomDateDeserializer.java
 */
package bld.commons.reflection.annotations.deserialize;

import java.io.IOException;
import java.security.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import bld.commons.exception.JpaServiceException;
import bld.commons.json.annotations.JsonDateFilter;
import bld.commons.json.annotations.JsonDateTimeZone;
import bld.commons.reflection.annotations.deserialize.data.DateFilterDeserializer;
import bld.commons.reflection.utils.DateUtils;
import bld.commons.reflection.utils.StaticApplicationContext;

/**
 * The Class CustomDateDeserializer.
 *
 * @param <T> the generic type
 */
@SuppressWarnings({ "serial", "unchecked" })
public class DateDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer {

	/** The env. */
	private Environment env = null;

	/** The date filter deserializer. */
	private DateFilterDeserializer dateFilterDeserializer = null;

	/** The simple date format. */
	private SimpleDateFormat simpleDateFormat = null;

	/** The Constant logger. */
	private final static Log logger = LogFactory.getLog(DateDeserializer.class);

	/**
	 * Instantiates a new custom date deserializer.
	 */
	public DateDeserializer() {
		super(Object.class);
		this.env = StaticApplicationContext.getBean(Environment.class);
	}

	/**
	 * Instantiates a new custom date deserializer.
	 *
	 * @param classDate the class date
	 * @param dateDeserializer the date deserializer
	 * @param simpleDateFormat the simple date format
	 */
	private DateDeserializer(Class<T> classDate, DateFilterDeserializer dateDeserializer, SimpleDateFormat simpleDateFormat) {
		super(classDate);
		this.dateFilterDeserializer = dateDeserializer;
		this.simpleDateFormat = simpleDateFormat;
		this.env = StaticApplicationContext.getBean(Environment.class);
	}

	/**
	 * Gets the date.
	 *
	 * @param dateString the date string
	 * @return the date
	 * @throws JpaServiceException the jpa service exception
	 */
	protected Date getDate(String dateString) throws JpaServiceException {
		try {
			Date date=this.simpleDateFormat.parse(dateString);
			return DateUtils.sumDate(date, this.dateFilterDeserializer.getAddYear(), this.dateFilterDeserializer.getAddMonth(), this.dateFilterDeserializer.getAddWeek(), this.dateFilterDeserializer.getAddDay(), this.dateFilterDeserializer.getAddHour(), this.dateFilterDeserializer.getAddMinute(), this.dateFilterDeserializer.getAddSecond());
		} catch (ParseException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new JpaServiceException(e);
		}
	}

	/**
	 * Sets the simple date format.
	 *
	 * @param timeZone the time zone
	 * @param format the format
	 */
	private void setSimpleDateFormat(TimeZone timeZone, String format) {
		this.simpleDateFormat = new SimpleDateFormat(format);
		this.simpleDateFormat.setTimeZone(timeZone);
	}

	/**
	 * Deserialize.
	 *
	 * @param p the p
	 * @param ctxt the ctxt
	 * @return the t
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JsonProcessingException the json processing exception
	 */
	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String dateString = p.getText();
		T value = null;
		Date date = this.getDate(dateString);
		if (Date.class.isAssignableFrom(super._valueClass))
			value = (T) date;
		if (Calendar.class.isAssignableFrom(this._valueClass))
			value = (T) DateUtils.dateToCalendar(date);
		if (Timestamp.class.isAssignableFrom(super._valueClass))
			value = (T) DateUtils.dateToTimestamp(date);
		return value;
	}

	/**
	 * Creates the contextual.
	 *
	 * @param ctxt the ctxt
	 * @param property the property
	 * @return the json deserializer
	 * @throws JsonMappingException the json mapping exception
	 */
	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JsonDateTimeZone dateTimeZone = property.getAnnotation(JsonDateTimeZone.class);
		JsonDateFilter dateFilter = property.getAnnotation(JsonDateFilter.class);
		if (dateTimeZone != null)
			this.dateFilterDeserializer = new DateFilterDeserializer(dateTimeZone.timeZone(), dateTimeZone.format());
		else if (dateFilter != null)
			this.dateFilterDeserializer = new DateFilterDeserializer(dateFilter.timeZone(), dateFilter.format(), dateFilter.addYear(), dateFilter.addMonth(), dateFilter.addWeek(), dateFilter.addDay(), dateFilter.addHour(),
					dateFilter.addMinute(), dateFilter.addSecond());
		if (DateUtils.ENV_TIME_ZONE.equals(this.dateFilterDeserializer.getTimeZone())) {
			if (this.env.getProperty(DateUtils.PROPS_TIME_ZONE) == null)
				this.setSimpleDateFormat(TimeZone.getDefault(), this.dateFilterDeserializer.getFormat());
			else
				this.setSimpleDateFormat(TimeZone.getTimeZone(this.env.getProperty(DateUtils.PROPS_TIME_ZONE)), this.dateFilterDeserializer.getFormat());
		} else {
			String timeZone = this.dateFilterDeserializer.getTimeZone().replace("${", "").replace("}", "");
			this.setSimpleDateFormat(TimeZone.getTimeZone(this.env.getProperty(timeZone, timeZone)), this.dateFilterDeserializer.getFormat());
		}

		if (property.getType() != null && property.getType().getRawClass() != null)
			return new DateDeserializer<>(property.getType().getRawClass(), this.dateFilterDeserializer, this.simpleDateFormat);
		else
			return this;
	}

}

