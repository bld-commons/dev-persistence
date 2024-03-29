package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.CalendarioSpeedy;
import com.bld.persistence.core.repository.CalendarioSpeedyRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

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