package bld.commons.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan({"bld.commons.config","bld.commons.reflection","bld.commons.repository","bld.commons.service"})
public class EnableJpaServiceConfiguration {

}
