package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.CalendarioSpeedy;
import com.bld.persistence.core.domain.CalendarioSpeedyRepository;
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
public  class CalendarioSpeedyServiceImpl extends JpaServiceImpl<CalendarioSpeedy,Long> implements CalendarioSpeedyService{


	

	@Autowired
    private CalendarioSpeedyRepository calendarioSpeedyRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From CalendarioSpeedy calendarioSpeedy join fetch calendarioSpeedy.speedy speedy "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct calendarioSpeedy"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(calendarioSpeedy)"+FROM_BY_FILTER;
    
	
    
    
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
        map.put("idSpeedy", " and speedy.idSpeedy in (:idSpeedy) ");
        map.put("dataLavoroBeforeEqual", " and calendarioSpeedy.dataLavoro<=:dataLavoroBeforeEqual ");
        map.put("dataLavoroAfterEqual", " and calendarioSpeedy.dataLavoro>=:dataLavoroAfterEqual ");
        map.put("dataLavoroBefore", " and calendarioSpeedy.dataLavoro<:dataLavoroBefore ");
        map.put("dataLavoroAfter", " and calendarioSpeedy.dataLavoro>:dataLavoroAfter ");
        map.put("dataLavoro", " and calendarioSpeedy.dataLavoro=:dataLavoro ");
        map.put("idCalendarioSpeedy", " and calendarioSpeedy.idCalendarioSpeedy in (:idCalendarioSpeedy) ");
        map.put("id", " and calendarioSpeedy.idCalendarioSpeedy in (:idCalendarioSpeedy) ");
        map.put("idOrdine", " and ordines.idOrdine in (:ordines) ");
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
        addJoinOneToMany("idOrdine", " left join fetch calendarioSpeedy.ordines ordines ");
    }
	@Override
    protected  JpaRepository<CalendarioSpeedy,Long> getJpaRepository() {
        return calendarioSpeedyRepository;
    }

}