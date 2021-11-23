package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.Utente;
import org.springframework.stereotype.Repository;

@Repository
public  interface UtenteRepository extends BaseJpaRepository<Utente,Long>{

}