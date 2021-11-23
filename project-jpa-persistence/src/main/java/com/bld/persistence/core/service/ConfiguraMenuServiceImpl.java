package com.bld.persistence.core.service;

import bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.ConfiguraMenuRepository;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.ConfiguraMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import bld.commons.processor.annotations.QueryBuilder;

@Service
@Transactional
@QueryBuilder
public  class ConfiguraMenuServiceImpl extends JpaServiceImpl<ConfiguraMenu,Long> implements ConfiguraMenuService{
	@Autowired
    private ConfiguraMenuRepository configuraMenuRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
    
	@Override
    protected  JpaRepository<ConfiguraMenu,Long> getJpaRepository() {
        return this.configuraMenuRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}