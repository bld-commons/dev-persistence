package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.TipoStatoOrdine;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface TipoStatoOrdineRepository extends BaseJpaRepository<TipoStatoOrdine,Long>{

}