package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.PostazioneCucina;
import bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface PostazioneCucinaRepository extends BaseJpaRepository<PostazioneCucina,Long>{

}