package com.bld.persistence.core.service;

import com.bld.persistence.core.domain.ConsegnaCapRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import com.bld.persistence.core.domain.ConsegnaCapPK;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.ConsegnaCap;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class ConsegnaCapServiceImpl extends JpaServiceImpl<ConsegnaCap,ConsegnaCapPK> implements ConsegnaCapService{


	

	@Autowired
    private ConsegnaCapRepository consegnaCapRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From ConsegnaCap consegnaCap join fetch consegnaCap.ristorante ristorante where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct consegnaCap"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(consegnaCap)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
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
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("updateTimestampBefore", " and consegnaCap.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and consegnaCap.updateTimestamp<= :updateTimestampAfter ");
        map.put("createTimestampBefore", " and consegnaCap.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and consegnaCap.createTimestamp<= :createTimestampAfter ");
        map.put("flagValido", " and consegnaCap.flagValido= :flagValido ");
        map.put("updateUser", " and consegnaCap.updateUser like :updateUser ");
        map.put("createUser", " and consegnaCap.createUser like :createUser ");
        map.put("cap", " and consegnaCap.id.cap in (:cap) ");
        map.put("idRistorante", " and consegnaCap.id.idRistorante in (:idRistorante) ");
        return map;
    }
	@Override
    protected  void mapOneToMany() {
    }
	@Override
    protected  JpaRepository<ConsegnaCap,ConsegnaCapPK> getJpaRepository() {
        return consegnaCapRepository;
    }

}