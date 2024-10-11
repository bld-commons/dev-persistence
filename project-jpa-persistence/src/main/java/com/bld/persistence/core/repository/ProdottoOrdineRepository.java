package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.ProdottoOrdine;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ProdottoOrdineRepository extends BaseJpaRepository<ProdottoOrdine,Long>{

}