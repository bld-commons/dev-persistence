package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.ModificaProdottoOrdine;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ModificaProdottoOrdineRepository extends BaseJpaRepository<ModificaProdottoOrdine,Long>{

}