package com.bld.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import bld.commons.config.annotation.EnableJpaService;
import bld.commons.processor.annotations.JpaBuilder;

@SpringBootApplication
@EnableJpaRepositories
@EnableJpaService
@JpaBuilder(repositoryPackage = "com.bld.persistence.core.repository", servicePackage = "com.bld.persistence.core.service")
public class ProjectJpaPersistenceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectJpaPersistenceApplication.class, args);
	}

}
