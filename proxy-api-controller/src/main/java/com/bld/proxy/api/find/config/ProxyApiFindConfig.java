package com.bld.proxy.api.find.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
@ComponentScan(basePackages = {"com.bld.proxy.api.find"})
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class ProxyApiFindConfig {


}
