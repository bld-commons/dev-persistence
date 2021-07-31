package bld.commons.reflection.annotations.serialize;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

@SuppressWarnings("serial")
public class CustomByteSerializer  extends StdSerializer<byte[]>  {

	 public CustomByteSerializer() {
	        this(null);
	    }
	  
	    public CustomByteSerializer(Class<byte[]> t) {
	        super(t);
	    }



	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(Base64.getEncoder().encodeToString(value));
	}

}
