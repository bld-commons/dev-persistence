package bld.commons.reflection.annotations.deserialize;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import bld.commons.json.annotations.MaxConsecutiveSpace;
import bld.commons.reflection.annotations.deserialize.data.MaxConsecutiveSpaceProps;

@SuppressWarnings("serial")
public class MaxConsecutiveSpaceDeserializer extends StdDeserializer<String> implements ContextualDeserializer{

	private MaxConsecutiveSpaceProps maxConsecutiveSpaceProps;
	
	public MaxConsecutiveSpaceDeserializer() {
		super(String.class);
	}


	protected MaxConsecutiveSpaceDeserializer(Class<String> src,MaxConsecutiveSpaceProps maxConsecutiveSpaceProps) {
		super(src);
		this.maxConsecutiveSpaceProps=maxConsecutiveSpaceProps;
	}

	@Override
	public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) throws JsonMappingException {
		MaxConsecutiveSpace maxConsecutiveSpace = property.getAnnotation(MaxConsecutiveSpace.class);
		MaxConsecutiveSpaceProps maxConsecutiveSpaceProps = new MaxConsecutiveSpaceProps(maxConsecutiveSpace.consecutive(), maxConsecutiveSpace.trim(),maxConsecutiveSpace.removeEndline(),maxConsecutiveSpace.removeAllSpaceType());
		return new MaxConsecutiveSpaceDeserializer(String.class, maxConsecutiveSpaceProps);
	}

	@Override
	public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String text=p.getText();
		if(StringUtils.isNotEmpty(text)) {
			if(maxConsecutiveSpaceProps.isRemoveAllSpaceType()) 
				text=text.replaceAll("\\s+", "");
			else {
				if(maxConsecutiveSpaceProps.isRemoveEndline())
					text=text.replace("\n", "");
				if(maxConsecutiveSpaceProps.isTrim())
					text=text.trim();
				String space="";
				for(int i=0;i<maxConsecutiveSpaceProps.getConsecutive();i++)
					space+=" ";
				text=removeSpace(space+" ", space, text);
			}	
		}
		return text;
	}

	
	private String removeSpace(String remveText,String replaceText,String text) {
		if(text.contains(remveText))
			text=removeSpace(remveText, replaceText, text.replace(remveText, replaceText));
		return text;
	}
	
	
}
