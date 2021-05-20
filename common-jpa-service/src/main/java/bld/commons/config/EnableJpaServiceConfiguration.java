/**
 * @author Francesco Baldi
 * @mail francesco.baldi1987@gmail.com
 * @class bld.commons.config.EnableJpaServiceConfiguration.java
 * 
 */
package bld.commons.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * The Class EnableJpaServiceConfiguration.
 */
@Configuration
@ComponentScan({"bld.commons.config","bld.commons.reflection","bld.commons.repository","bld.commons.service"})
public class EnableJpaServiceConfiguration {

}
