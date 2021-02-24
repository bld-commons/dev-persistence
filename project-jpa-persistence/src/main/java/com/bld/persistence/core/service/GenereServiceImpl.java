package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.GenereRepository;
import com.bld.persistence.core.domain.Genere;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class GenereServiceImpl extends JpaServiceImpl<Genere,Long> implements GenereService{


	

	@Autowired
    private GenereRepository genereRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Genere genere join fetch genere.postazioneCucina postazioneCucina "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct genere"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(genere)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  JpaRepository<Genere,Long> getJpaRepository() {
        return genereRepository;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idGenere", " and genere.idGenere in (:idGenere) ");
        map.put("id", " and genere.idGenere in (:idGenere) ");
        map.put("idPostazioneCucina", " and postazioneCucina.idPostazioneCucina in (:idPostazioneCucina) ");
        map.put("desGenere", " and genere.desGenere like :desGenere ");
        map.put("idConfiguraMenu", " and configuraMenus.idConfiguraMenu in (:configuraMenus) ");
        map.put("updateTimestampBeforeEqual", " and genere.updateTimestamp<=:updateTimestampBeforeEqual ");
        map.put("updateTimestampAfterEqual", " and genere.updateTimestamp>=:updateTimestampAfterEqual ");
        map.put("updateTimestampBefore", " and genere.updateTimestamp<:updateTimestampBefore ");
        map.put("updateTimestampAfter", " and genere.updateTimestamp>:updateTimestampAfter ");
        map.put("updateTimestamp", " and genere.updateTimestamp=:updateTimestamp ");
        map.put("createTimestampBeforeEqual", " and genere.createTimestamp<=:createTimestampBeforeEqual ");
        map.put("createTimestampAfterEqual", " and genere.createTimestamp>=:createTimestampAfterEqual ");
        map.put("createTimestampBefore", " and genere.createTimestamp<:createTimestampBefore ");
        map.put("createTimestampAfter", " and genere.createTimestamp>:createTimestampAfter ");
        map.put("createTimestamp", " and genere.createTimestamp=:createTimestamp ");
        map.put("flagValido", " and genere.flagValido= :flagValido ");
        map.put("idProdotto", " and prodottos.idProdotto in (:prodottos) ");
        map.put("createUser", " and genere.createUser like :createUser ");
        map.put("updateUser", " and genere.updateUser like :updateUser ");
        return map;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idConfiguraMenu", "  join fetch genere.configuraMenus configuraMenus ");
        addJoinOneToMany("idProdotto", "  join fetch genere.prodottos prodottos ");
    }

}