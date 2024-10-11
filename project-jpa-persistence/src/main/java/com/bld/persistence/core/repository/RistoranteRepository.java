package com.bld.persistence.core.repository;

import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;
import com.bld.persistence.core.domain.Ristorante;

@Repository
public  interface RistoranteRepository extends BaseJpaRepository<Ristorante,Long>{

}