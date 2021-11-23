package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import org.springframework.stereotype.Repository;

@Repository
public  interface ModificaProdottoOrdineRepository extends BaseJpaRepository<ModificaProdottoOrdine,Long>{

}