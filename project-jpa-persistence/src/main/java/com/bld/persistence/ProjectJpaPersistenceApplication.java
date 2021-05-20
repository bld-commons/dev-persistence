package com.bld.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import bld.commons.config.annotation.EnableJpaService;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaService
public class ProjectJpaPersistenceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(ProjectJpaPersistenceApplication.class, args);
	}

}
