package com.bld.persistence.core.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bld.persistence.core.domain.Ordine;
import com.bld.persistence.core.repository.OrdineRepository;

import bld.commons.processor.OperationType;
import bld.commons.processor.annotations.ConditionBuilder;
import bld.commons.processor.annotations.CustomConditionBuilder;
import bld.commons.processor.annotations.QueryBuilder;
import bld.commons.service.JpaServiceImpl;

@Service
@Transactional
@QueryBuilder(joins = { "ordine.cliente.tipoToponimo" }, conditions = { @ConditionBuilder(field = "ordine.ristorante.tipoToponimo.idTipoToponimo", operation = OperationType.IN, parameter = "idTipoToponimo"),
		@ConditionBuilder(field = "ordine.ristorante.speedies.utente.cognome", operation = OperationType.LIKE, parameter = "cognome") }, customConditions = {
				@CustomConditionBuilder(condition = " and tipoStatoOrdine.desTipoStatoOrdine like :desTipoStatoOrdine ", parameter = "desTipoStatoOrdine") })
public class OrdineServiceImpl extends JpaServiceImpl<Ordine, Long> implements OrdineService {
	@Autowired
	private OrdineRepository ordineRepository;

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	private NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	protected NamedParameterJdbcTemplate getJdbcTemplate() {
		return this.jdbcTemplate;
	}

	@Override
	protected JpaRepository<Ordine, Long> getJpaRepository() {
		return this.ordineRepository;
	}

	@Override
	protected EntityManager getEntityManager() {
		return this.entityManager;
	}

}