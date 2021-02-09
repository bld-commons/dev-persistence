package com.bld.persistence.core.service;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import com.bld.persistence.core.domain.StoricoOrdine;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.StoricoOrdinePK;
import com.bld.persistence.core.domain.StoricoOrdineRepository;
import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class StoricoOrdineServiceImpl extends JpaServiceImpl<StoricoOrdine,StoricoOrdinePK> implements StoricoOrdineService{


	

	@Autowired
    private StoricoOrdineRepository storicoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From StoricoOrdine storicoOrdine join fetch storicoOrdine.cliente cliente join fetch storicoOrdine.ristorante ristorante where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct storicoOrdine"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(storicoOrdine)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  JpaRepository<StoricoOrdine,StoricoOrdinePK> getJpaRepository() {
        return storicoOrdineRepository;
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
    protected  EntityManager getEntityManager() {
        return entityManager;
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
    protected  void mapOneToMany() {
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("createUser", " and storicoOrdine.createUser like :createUser ");
        map.put("codiceOrdine", " and storicoOrdine.id.codiceOrdine in (:codiceOrdine) ");
        map.put("idRistorante", " and storicoOrdine.id.idRistorante in (:idRistorante) ");
        map.put("idCliente", " and storicoOrdine.id.idCliente in (:idCliente) ");
        map.put("dataRicevutaBefore", " and storicoOrdine.dataRicevuta>= :dataRicevutaBefore ");
        map.put("dataRicevutaAfter", " and storicoOrdine.dataRicevuta<= :dataRicevutaAfter ");
        map.put("updateUser", " and storicoOrdine.updateUser like :updateUser ");
        map.put("updateTimestampBefore", " and storicoOrdine.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and storicoOrdine.updateTimestamp<= :updateTimestampAfter ");
        map.put("createTimestampBefore", " and storicoOrdine.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and storicoOrdine.createTimestamp<= :createTimestampAfter ");
        map.put("ricevuta", " and storicoOrdine.ricevuta in (:ricevuta) ");
        map.put("idCliente", " and cliente.idCliente in (:idCliente) ");
        map.put("idRistorante", " and ristorante.idRistorante in (:idRistorante) ");
        return map;
    }

}