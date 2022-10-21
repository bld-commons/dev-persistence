package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.PostazioneCucinaRepository;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.PostazioneCucina;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

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