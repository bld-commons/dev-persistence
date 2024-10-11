package com.bld.persistence.core.service;

import com.bld.persistence.core.repository.ConsegnaCapRepository;
import com.bld.persistence.core.domain.ConsegnaCap;
import com.bld.commons.service.JpaServiceImpl;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.commons.processor.annotations.QueryBuilder;
import com.bld.persistence.core.domain.ConsegnaCapPK;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder
public  class ConsegnaCapServiceImpl extends JpaServiceImpl<ConsegnaCap,ConsegnaCapPK> implements ConsegnaCapService{
	@Autowired
    private ConsegnaCapRepository consegnaCapRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ConsegnaCap,ConsegnaCapPK> getJpaRepository() {
        return this.consegnaCapRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}