package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import com.bld.persistence.core.domain.ConfiguraMenuRepository;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.ConfiguraMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class ConfiguraMenuServiceImpl extends JpaServiceImpl<ConfiguraMenu,Long> implements ConfiguraMenuService{


	

	@Autowired
    private ConfiguraMenuRepository configuraMenuRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From ConfiguraMenu configuraMenu join fetch configuraMenu.genere genere join fetch configuraMenu.prodotto prodotto where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct configuraMenu"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(configuraMenu)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  void mapOneToMany() {
    }
	@Override
    protected  JpaRepository<ConfiguraMenu,Long> getJpaRepository() {
        return configuraMenuRepository;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idGenere", " and genere.idGenere in (:idGenere) ");
        map.put("idProdotto", " and prodotto.idProdotto in (:idProdotto) ");
        map.put("quantita", " and configuraMenu.quantita in (:quantita) ");
        map.put("idConfiguraMenu", " and configuraMenu.idConfiguraMenu in (:idConfiguraMenu) ");
        map.put("id", " and configuraMenu.idConfiguraMenu in (:idConfiguraMenu) ");
        return map;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}