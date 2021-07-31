package bld.commons.reflection.annotations.serialize;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.core.env.Environment;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import bld.commons.json.annotations.JsonDateTimeZone;
import bld.commons.reflection.utils.DateUtils;
import bld.commons.reflection.utils.StaticApplicationContext;

@SuppressWarnings("serial")
public class CustomDateSerializer<T> extends StdSerializer<T> implements ContextualSerializer {

	private Environment env = null;

	protected JsonDateTimeZone dateTimeZone = null;

	private SimpleDateFormat simpleDateFormat = null;


	public CustomDateSerializer() {
		this(null);
		this.env=StaticApplicationContext.getBean(Environment.class);
	}

	public CustomDateSerializer(Class<T> t) {
		super(t);
		this.env=StaticApplicationContext.getBean(Environment.class);
	}

	private CustomDateSerializer(Class<T> classDate, JsonDateTimeZone dateTimeZone, SimpleDateFormat simpleDateFormat) {
		super(classDate);
		this.dateTimeZone = dateTimeZone;
		this.simpleDateFormat = simpleDateFormat;
		this.env = StaticApplicationContext.getBean(Environment.class);
	}
	
	
	public String formatDate(T date) {
		String dateString=null;
		if(date instanceof Calendar)
			dateString=this.simpleDateFormat.format(DateUtils.calendarToDate((Calendar)date));
		else if(date instanceof Date)
			dateString=this.simpleDateFormat.format((Date)date);
		else if(date instanceof Timestamp)
			dateString=this.simpleDateFormat.format(DateUtils.timestampToDate((Timestamp)date));
		return dateString;
	}
	
	

	@Override
	public void serialize(T date, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(this.formatDate(date));
	}

	private void setSimpleDateFormat(TimeZone timeZone, String format) {
		this.simpleDateFormat = new SimpleDateFormat(format);
		this.simpleDateFormat.setTimeZone(timeZone);
	}

	@Override
	public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
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
			return new CustomDateSerializer<>(property.getType().getRawClass(), this.dateTimeZone, this.simpleDateFormat);
		else
			return this;
	}

}
