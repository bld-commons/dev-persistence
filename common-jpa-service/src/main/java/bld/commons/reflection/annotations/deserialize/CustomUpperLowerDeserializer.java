package bld.commons.reflection.annotations.deserialize;

import java.io.IOException;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import bld.commons.json.annotations.JsonUpperLowerCase;
import bld.commons.reflection.type.UpperLowerType;

@SuppressWarnings("serial")
public class CustomUpperLowerDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

	private UpperLowerType upperLower;

	 public CustomUpperLowerDeserializer() {
	       super(String.class);
	   }
	
	protected CustomUpperLowerDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		JsonUpperLowerCase jsonUpperLower = property.getAnnotation(JsonUpperLowerCase.class);
		this.upperLower=jsonUpperLower.value();
		return this;
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String word=p.getText();
		switch(this.upperLower) {
		case LOWER:
			word=word.toLowerCase();
			break;
		case UPPER:
			word=word.toUpperCase();
			break;
		case NONE:
		default:
			break;
		
		}
		return word;
	}

}
