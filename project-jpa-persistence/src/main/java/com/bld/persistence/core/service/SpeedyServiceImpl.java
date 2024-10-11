package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.SpeedyRepository;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.Speedy;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class SpeedyServiceImpl extends JpaServiceImpl<Speedy,Long> implements SpeedyService{
	@Autowired
    private SpeedyRepository speedyRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Speedy,Long> getJpaRepository() {
        return this.speedyRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}