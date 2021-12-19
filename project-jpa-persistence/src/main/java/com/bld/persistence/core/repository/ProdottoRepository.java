package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Prodotto;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface ProdottoRepository extends BaseJpaRepository<Prodotto,Long>{

}