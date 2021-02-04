package com.bld.persistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.bld.persistence.core.domain.ClienteRepository;

@SpringBootApplication
@EnableJpaRepositories
public class ProjectJpaPersistenceApplication {

	@Autowired
	private ClienteRepository clienteRepo;
	
	
	public static void main(String[] args) {
		SpringApplication.run(ProjectJpaPersistenceApplication.class, args);
	}

}
