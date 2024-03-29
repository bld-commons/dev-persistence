package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.PostazioneCucina;
import com.bld.persistence.core.repository.PostazioneCucinaRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class PostazioneCucinaServiceImpl extends JpaServiceImpl<PostazioneCucina,Long> implements PostazioneCucinaService{
	@Autowired
    private PostazioneCucinaRepository postazioneCucinaRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<PostazioneCucina,Long> getJpaRepository() {
        return this.postazioneCucinaRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}