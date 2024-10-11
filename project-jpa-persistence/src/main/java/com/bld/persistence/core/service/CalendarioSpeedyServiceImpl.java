package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.CalendarioSpeedy;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.repository.CalendarioSpeedyRepository;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class CalendarioSpeedyServiceImpl extends JpaServiceImpl<CalendarioSpeedy,Long> implements CalendarioSpeedyService{
	@Autowired
    private CalendarioSpeedyRepository calendarioSpeedyRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<CalendarioSpeedy,Long> getJpaRepository() {
        return this.calendarioSpeedyRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}