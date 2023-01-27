/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.config.EnableJpaServiceConfiguration.java
 * 
 */
package bld.commons.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.bld.commons.utils.config.annotation.EnableCommonUtils;

/**
 * The Class EnableJpaServiceConfiguration.
 */
@Configuration
@EnableCommonUtils
@ComponentScan({"bld.commons.config","bld.commons.reflection","bld.commons.repository","bld.commons.service","bld.commons.workspace","bld.commons.annotations"})
public class EnableJpaServiceConfiguration {
	
	
}
