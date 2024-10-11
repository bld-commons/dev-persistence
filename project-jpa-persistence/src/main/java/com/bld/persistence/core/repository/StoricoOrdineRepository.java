package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.StoricoOrdine;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;
import com.bld.persistence.core.domain.StoricoOrdinePK;

@Repository
public  interface StoricoOrdineRepository extends BaseJpaRepository<StoricoOrdine,StoricoOrdinePK>{

}