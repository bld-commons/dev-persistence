package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import bld.commons.persistence.base.service.JpaServiceImpl;
import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.ModificaProdottoOrdineRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class ModificaProdottoOrdineServiceImpl extends JpaServiceImpl<ModificaProdottoOrdine,Long> implements ModificaProdottoOrdineService{


	

	@Autowired
    private ModificaProdottoOrdineRepository modificaProdottoOrdineRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From ModificaProdottoOrdine modificaProdottoOrdine join fetch modificaProdottoOrdine.prodottoOrdine prodottoOrdine "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct modificaProdottoOrdine"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(modificaProdottoOrdine)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  void mapOneToMany() {
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("quantita", " and modificaProdottoOrdine.quantita in (:quantita) ");
        map.put("createUser", " and modificaProdottoOrdine.createUser like :createUser ");
        map.put("idModificaProdottoOrdine", " and modificaProdottoOrdine.idModificaProdottoOrdine in (:idModificaProdottoOrdine) ");
        map.put("id", " and modificaProdottoOrdine.idModificaProdottoOrdine in (:idModificaProdottoOrdine) ");
        map.put("updateUser", " and modificaProdottoOrdine.updateUser like :updateUser ");
        map.put("createTimestampBefore", " and modificaProdottoOrdine.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and modificaProdottoOrdine.createTimestamp<= :createTimestampAfter ");
        map.put("flagValido", " and modificaProdottoOrdine.flagValido= :flagValido ");
        map.put("updateTimestampBefore", " and modificaProdottoOrdine.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and modificaProdottoOrdine.updateTimestamp<= :updateTimestampAfter ");
        map.put("idProdottoOrdine", " and prodottoOrdine.idProdottoOrdine in (:idProdottoOrdine) ");
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
    protected  JpaRepository<ModificaProdottoOrdine,Long> getJpaRepository() {
        return modificaProdottoOrdineRepository;
    }

}