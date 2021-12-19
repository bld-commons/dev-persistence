package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.ConsegnaCap;
import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.ConsegnaCapPK;
import org.springframework.stereotype.Repository;

@Repository
public  interface ConsegnaCapRepository extends BaseJpaRepository<ConsegnaCap,ConsegnaCapPK>{

}