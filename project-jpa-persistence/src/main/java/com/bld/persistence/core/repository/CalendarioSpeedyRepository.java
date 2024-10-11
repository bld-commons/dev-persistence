package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.CalendarioSpeedy;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface CalendarioSpeedyRepository extends BaseJpaRepository<CalendarioSpeedy,Long>{

}