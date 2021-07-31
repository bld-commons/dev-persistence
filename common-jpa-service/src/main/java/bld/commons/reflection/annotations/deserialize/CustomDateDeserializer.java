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
import bld.commons.json.annotations.JsonDateTimeZone;
import bld.commons.reflection.utils.DateUtils;
import bld.commons.reflection.utils.StaticApplicationContext;

@SuppressWarnings({"serial","unchecked"})
public class CustomDateDeserializer<T> extends StdDeserializer<T> implements ContextualDeserializer {

	private Environment env=null;

	protected JsonDateTimeZone dateTimeZone = null;

	private SimpleDateFormat simpleDateFormat = null;
	
	private final static Log logger = LogFactory.getLog(CustomDateDeserializer.class);

	public CustomDateDeserializer() {
		super(Object.class);
		this.env=StaticApplicationContext.getBean(Environment.class);
	}

	private CustomDateDeserializer(Class<T> classDate,JsonDateTimeZone dateTimeZone,SimpleDateFormat simpleDateFormat) {
		super(classDate);
		this.dateTimeZone=dateTimeZone;
		this.simpleDateFormat=simpleDateFormat;
		this.env=StaticApplicationContext.getBean(Environment.class);
	}

	protected Date getDate(String dateString) throws JpaServiceException {
		try {
			return this.simpleDateFormat.parse(dateString);
		} catch (ParseException e) {
			logger.error(ExceptionUtils.getStackTrace(e));
			throw new JpaServiceException(e);
		}
	}

	private void setSimpleDateFormat(TimeZone timeZone, String format) {
		this.simpleDateFormat = new SimpleDateFormat(format);
		this.simpleDateFormat.setTimeZone(timeZone);
	}

	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String dateString = p.getText();
		T value = null;
		Date date = this.getDate(dateString);
		if(Date.class.isAssignableFrom(super._valueClass))
			value=(T)date;
		if(Calendar.class.isAssignableFrom(this._valueClass))
			value=(T)DateUtils.dateToCalendar(date);
		if(Timestamp.class.isAssignableFrom(super._valueClass))
			value=(T)DateUtils.dateToTimestamp(date);
		return value;
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		this.dateTimeZone = property.getAnnotation(JsonDateTimeZone.class);
		if (DateUtils.ENV_TIME_ZONE.equals(this.dateTimeZone.timeZone())) {
			if (this.env.getProperty(DateUtils.PROPS_TIME_ZONE) == null)
				this.setSimpleDateFormat(TimeZone.getDefault(), this.dateTimeZone.format());
			else
				this.setSimpleDateFormat(TimeZone.getTimeZone(this.env.getProperty(DateUtils.PROPS_TIME_ZONE)), this.dateTimeZone.format());
		} else {
			String timeZone=this.dateTimeZone.timeZone().replace("${", "").replace("}", "");
			this.setSimpleDateFormat(TimeZone.getTimeZone(this.env.getProperty(timeZone, timeZone)), this.dateTimeZone.format());
		}
			
		if (property.getType() != null && property.getType().getRawClass() != null)
			return new CustomDateDeserializer<>(property.getType().getRawClass(),this.dateTimeZone,this.simpleDateFormat);
		else
			return this;
	}
	
	

	

}
