package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.CalendarioSpeedy;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface CalendarioSpeedyRepository extends BaseJpaRepository<CalendarioSpeedy,Long>{

}