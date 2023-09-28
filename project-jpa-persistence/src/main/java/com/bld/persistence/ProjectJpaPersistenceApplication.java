package com.bld.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.security.reactive.ReactiveManagementWebSecurityAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.security.servlet.ManagementWebSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bld.exception.audit.client.config.annotation.EnableExceptionAuditClient;
import com.bld.proxy.api.find.config.annotation.EnableProxyApiController;

import bld.commons.config.annotation.EnableJpaService;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class,ManagementWebSecurityAutoConfiguration.class, ReactiveSecurityAutoConfiguration.class, ReactiveManagementWebSecurityAutoConfiguration.class })
@EnableJpaRepositories(basePackages = "com.bld.persistence.core")
@EnableJpaService
@EnableProxyApiController(basePackages = "com.bld")
@EnableExceptionAuditClient
public class ProjectJpaPersistenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectJpaPersistenceApplication.class, args);
	}

}
