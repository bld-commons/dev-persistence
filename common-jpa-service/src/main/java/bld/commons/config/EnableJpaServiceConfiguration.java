/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.config.EnableJpaServiceConfiguration.java
 * 
 */
package bld.commons.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class EnableJpaServiceConfiguration.
 */
@Configuration
@ComponentScan({"bld.commons.config","bld.commons.reflection","bld.commons.repository","bld.commons.service","bld.commons.workspace","bld.commons.annotations"})
public class EnableJpaServiceConfiguration {
	
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	    return mapper;
	}
}
