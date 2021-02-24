package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import com.bld.persistence.core.domain.Speedy;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.SpeedyRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class SpeedyServiceImpl extends JpaServiceImpl<Speedy,Long> implements SpeedyService{


	

	@Autowired
    private SpeedyRepository speedyRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Speedy speedy join fetch speedy.utente utente join fetch speedy.ristorante ristorante "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct speedy"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(speedy)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
    }
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  JpaRepository<Speedy,Long> getJpaRepository() {
        return speedyRepository;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idSpeedy", " and speedy.idSpeedy in (:idSpeedy) ");
        map.put("id", " and speedy.idSpeedy in (:idSpeedy) ");
        map.put("createUser", " and speedy.createUser like :createUser ");
        map.put("updateUser", " and speedy.updateUser like :updateUser ");
        map.put("createTimestampBeforeEqual", " and speedy.createTimestamp<=:createTimestampBeforeEqual ");
        map.put("createTimestampAfterEqual", " and speedy.createTimestamp>=:createTimestampAfterEqual ");
        map.put("createTimestampBefore", " and speedy.createTimestamp<:createTimestampBefore ");
        map.put("createTimestampAfter", " and speedy.createTimestamp>:createTimestampAfter ");
        map.put("createTimestamp", " and speedy.createTimestamp=:createTimestamp ");
        map.put("flagValido", " and speedy.flagValido= :flagValido ");
        map.put("updateTimestampBeforeEqual", " and speedy.updateTimestamp<=:updateTimestampBeforeEqual ");
        map.put("updateTimestampAfterEqual", " and speedy.updateTimestamp>=:updateTimestampAfterEqual ");
        map.put("updateTimestampBefore", " and speedy.updateTimestamp<:updateTimestampBefore ");
        map.put("updateTimestampAfter", " and speedy.updateTimestamp>:updateTimestampAfter ");
        map.put("updateTimestamp", " and speedy.updateTimestamp=:updateTimestamp ");
        map.put("idUtente", " and utente.idUtente in (:idUtente) ");
        map.put("idCalendarioSpeedy", " and calendarioSpeedies.idCalendarioSpeedy in (:calendarioSpeedies) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("numero", " and speedy.numero like :numero ");
        return map;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idCalendarioSpeedy", "  join fetch speedy.calendarioSpeedies calendarioSpeedies ");
    }

}