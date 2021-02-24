package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.Ingrediente;
import com.bld.persistence.core.domain.IngredienteRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class IngredienteServiceImpl extends JpaServiceImpl<Ingrediente,Long> implements IngredienteService{


	

	@Autowired
    private IngredienteRepository ingredienteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Ingrediente ingrediente join fetch ingrediente.ristorante ristorante "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct ingrediente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(ingrediente)"+FROM_BY_FILTER;
    
	
    
    
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
        map.put("idIngrediente", " and ingrediente.idIngrediente in (:idIngrediente) ");
        map.put("id", " and ingrediente.idIngrediente in (:idIngrediente) ");
        map.put("updateTimestampBeforeEqual", " and ingrediente.updateTimestamp<=:updateTimestampBeforeEqual ");
        map.put("updateTimestampAfterEqual", " and ingrediente.updateTimestamp>=:updateTimestampAfterEqual ");
        map.put("updateTimestampBefore", " and ingrediente.updateTimestamp<:updateTimestampBefore ");
        map.put("updateTimestampAfter", " and ingrediente.updateTimestamp>:updateTimestampAfter ");
        map.put("updateTimestamp", " and ingrediente.updateTimestamp=:updateTimestamp ");
        map.put("createTimestampBeforeEqual", " and ingrediente.createTimestamp<=:createTimestampBeforeEqual ");
        map.put("createTimestampAfterEqual", " and ingrediente.createTimestamp>=:createTimestampAfterEqual ");
        map.put("createTimestampBefore", " and ingrediente.createTimestamp<:createTimestampBefore ");
        map.put("createTimestampAfter", " and ingrediente.createTimestamp>:createTimestampAfter ");
        map.put("createTimestamp", " and ingrediente.createTimestamp=:createTimestamp ");
        map.put("flagValido", " and ingrediente.flagValido= :flagValido ");
        map.put("updateUser", " and ingrediente.updateUser like :updateUser ");
        map.put("createUser", " and ingrediente.createUser like :createUser ");
        map.put("desIngrediente", " and ingrediente.desIngrediente like :desIngrediente ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        return map;
    }
	@Override
    protected  void mapOneToMany() {
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
    protected  JpaRepository<Ingrediente,Long> getJpaRepository() {
        return ingredienteRepository;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
    }

}