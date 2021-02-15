package com.bld.persistence.core.service;

import org.springframework.stereotype.Service;
import java.util.Map;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import com.bld.persistence.core.domain.UtenteRepository;
import bld.commons.persistence.base.service.JpaServiceImpl;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import com.bld.persistence.core.domain.Utente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public  class UtenteServiceImpl extends JpaServiceImpl<Utente,Long> implements UtenteService{


	

	@Autowired
    private UtenteRepository utenteRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Autowired
    private NamedParameterJdbcTemplate jdbcTemplate;
    
    private final static Map<String,String> MAP_CONDITIONS=getMapConditions();
    
    private final static String FROM_BY_FILTER=" From Utente utente left join fetch utente.cliente cliente join fetch utente.tipoUtente tipoUtente "+ONE_TO_MANY+" where 1=1 ";
    
    private final static String SELECT_BY_FILTER="select distinct utente"+FROM_BY_FILTER;
    
    private final static String COUNT_BY_FILTER="select distinct count(utente)"+FROM_BY_FILTER;
    
	
    
    
	@Override
    protected  EntityManager getEntityManager() {
        return entityManager;
    }
	@Override
    protected  void mapOneToMany() {
        addJoinOneToMany("idSpeedy", "  join fetch utente.speedies speedies ");
    }
    private static  Map<String,String> getMapConditions() {
        Map<String,String> map=new HashMap<>();
        map.put("idUtente", " and utente.idUtente in (:idUtente) ");
        map.put("id", " and utente.idUtente in (:idUtente) ");
        map.put("email", " and utente.email like :email ");
        map.put("createUser", " and utente.createUser like :createUser ");
        map.put("idCliente", " and cliente.idCliente in (:idCliente) ");
        map.put("password", " and utente.password like :password ");
        map.put("updateTimestampBefore", " and utente.updateTimestamp>= :updateTimestampBefore ");
        map.put("updateTimestampAfter", " and utente.updateTimestamp<= :updateTimestampAfter ");
        map.put("createTimestampBefore", " and utente.createTimestamp>= :createTimestampBefore ");
        map.put("createTimestampAfter", " and utente.createTimestamp<= :createTimestampAfter ");
        map.put("idSpeedy", " and speedies.idSpeedy in (:speedies) ");
        map.put("confermaRegistrazione", " and utente.confermaRegistrazione= :confermaRegistrazione ");
        map.put("updateUser", " and utente.updateUser like :updateUser ");
        map.put("nome", " and utente.nome like :nome ");
        map.put("dataNascitaBefore", " and utente.dataNascita>= :dataNascitaBefore ");
        map.put("dataNascitaAfter", " and utente.dataNascita<= :dataNascitaAfter ");
        map.put("cognome", " and utente.cognome like :cognome ");
        map.put("codFiscale", " and utente.codFiscale like :codFiscale ");
        map.put("idTipoUtente", " and tipoUtente.idTipoUtente in (:idTipoUtente) ");
        return map;
    }
	@Override
    protected  JpaRepository<Utente,Long> getJpaRepository() {
        return utenteRepository;
    }
	@Override
    protected  Map<String,String> mapConditions() {
        return MAP_CONDITIONS;
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
    protected  String selectByFilter() {
        return SELECT_BY_FILTER;
    }

}