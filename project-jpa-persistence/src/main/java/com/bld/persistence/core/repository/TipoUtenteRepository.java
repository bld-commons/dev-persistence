package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoUtente;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoUtenteRepository extends BaseJpaRepository<TipoUtente,Long>{

}