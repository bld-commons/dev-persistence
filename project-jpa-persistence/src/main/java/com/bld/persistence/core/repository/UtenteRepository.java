package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Utente;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface UtenteRepository extends BaseJpaRepository<Utente,Long>{

}