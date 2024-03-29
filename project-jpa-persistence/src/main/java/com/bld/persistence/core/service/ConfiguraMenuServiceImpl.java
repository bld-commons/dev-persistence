package com.bld.persistence.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.ConfiguraMenu;
import com.bld.persistence.core.repository.ConfiguraMenuRepository;

import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@Transactional
@QueryBuilder
public  class ConfiguraMenuServiceImpl extends JpaServiceImpl<ConfiguraMenu,Long> implements ConfiguraMenuService{
	@Autowired
    private ConfiguraMenuRepository configuraMenuRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<ConfiguraMenu,Long> getJpaRepository() {
        return this.configuraMenuRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}