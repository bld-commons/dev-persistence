/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.reflection.annotations.serialize.CustomByteSerializer.java
 */
package bld.commons.reflection.annotations.serialize;

import java.io.IOException;
import java.util.Base64;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

/**
 * The Class CustomByteSerializer.
 */
@SuppressWarnings("serial")
public class CustomByteSerializer  extends StdSerializer<byte[]>  {

	 /**
 	 * Instantiates a new custom byte serializer.
 	 */
 	public CustomByteSerializer() {
	        this(null);
	    }
	  
	    /**
    	 * Instantiates a new custom byte serializer.
    	 *
    	 * @param t the t
    	 */
    	public CustomByteSerializer(Class<byte[]> t) {
	        super(t);
	    }



	/**
	 * Serialize.
	 *
	 * @param value the value
	 * @param gen the gen
	 * @param provider the provider
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	public void serialize(byte[] value, JsonGenerator gen, SerializerProvider provider) throws IOException {
		gen.writeString(Base64.getEncoder().encodeToString(value));
	}

}
