/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.deserialize.UpperLowerDeserializer.java
 */
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

/**
 * The Class UpperLowerDeserializer.
 */
@SuppressWarnings("serial")
public class UpperLowerDeserializer extends StdDeserializer<String> implements ContextualDeserializer {

	/** The upper lower. */
	private UpperLowerType upperLower;

	/**
	 * Instantiates a new upper lower deserializer.
	 */
	public UpperLowerDeserializer() {
		super(String.class);
	}

	/**
	 * Instantiates a new upper lower deserializer.
	 *
	 * @param vc the vc
	 */
	protected UpperLowerDeserializer(Class<?> vc) {
		super(vc);
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
		JsonUpperLowerCase jsonUpperLower = property.getAnnotation(JsonUpperLowerCase.class);
		this.upperLower = jsonUpperLower.value();
		return this;
	}

	/**
	 * Deserialize.
	 *
	 * @param p the p
	 * @param ctxt the ctxt
	 * @return the string
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws JacksonException the jackson exception
	 */
	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JacksonException {
		String word = p.getText();
		switch (this.upperLower) {
		case LOWER:
			word = word.toLowerCase();
			break;
		case UPPER:
			word = word.toUpperCase();
			break;
		case NONE:
		default:
			break;

		}
		return word;
	}

}
