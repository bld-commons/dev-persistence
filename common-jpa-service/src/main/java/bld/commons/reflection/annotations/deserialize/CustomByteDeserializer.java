package bld.commons.reflection.annotations.deserialize;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class CustomByteDeserializer extends JsonDeserializer<byte[]> {

	@Override
	public byte[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		String file=p.getText();
		String partSeparator = ",";
		if(file.contains(partSeparator))
			file=file.substring(file.indexOf(partSeparator)+1);
		return Base64.getDecoder().decode(file);
	}

}
