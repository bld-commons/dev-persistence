package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.PostazioneCucina;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.PostazioneCucinaRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class PostazioneCucinaServiceImpl extends JpaServiceImpl<PostazioneCucina,Long> implements PostazioneCucinaService{


	

	@Autowired
    private PostazioneCucinaRepository postazioneCucinaRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From PostazioneCucina postazioneCucina join fetch postazioneCucina.ristorante ristorante "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct postazioneCucina"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(postazioneCucina)"+FROM_BY_FILTER;
    
	
    
    
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("createUser", " and postazioneCucina.createUser like :createUser ");
        map.put("updateUser", " and postazioneCucina.updateUser like :updateUser ");
        map.put("updateTimestampBefore", " and postazioneCucina.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and postazioneCucina.updateTimestamp<= :updateTimestampAfter ");
        map.put("createTimestampBefore", " and postazioneCucina.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and postazioneCucina.createTimestamp<= :createTimestampAfter ");
        map.put("flagValido", " and postazioneCucina.flagValido= :flagValido ");
        map.put("idGenere", " and generes.idGenere in (:generes) ");
        map.put("idPostazioneCucina", " and postazioneCucina.idPostazioneCucina in (:idPostazioneCucina) ");
        map.put("id", " and postazioneCucina.idPostazioneCucina in (:idPostazioneCucina) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        map.put("desPostazioneCucina", " and postazioneCucina.desPostazioneCucina like :desPostazioneCucina ");
        return map;
    }
	@Override
    protected  NamedParameterJdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
	@Override
    protected  String countByFilter() {
        return COUNT_BY_FILTER;
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
    protected  void mapOneToMany() {
        addJoinOneToMany("idGenere", "  join fetch postazioneCucina.generes generes ");
    }
	@Override
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }
	@Override
    protected  JpaRepository<PostazioneCucina,Long> getJpaRepository() {
        return postazioneCucinaRepository;
    }

}