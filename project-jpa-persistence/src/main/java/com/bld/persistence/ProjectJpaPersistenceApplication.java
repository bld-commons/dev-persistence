package com.bld.persistence;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "bld")
@EnableJpaRepositories
public class ProjectJpaPersistenceApplication {

	
	public static void main(String[] args) {
		SpringApplication.run(ProjectJpaPersistenceApplication.class, args);
	}

}
