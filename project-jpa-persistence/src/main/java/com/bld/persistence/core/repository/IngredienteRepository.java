package com.bld.persistence.core.repository;

import bld.commons.repository.BaseJpaRepository;
import com.bld.persistence.core.domain.Ingrediente;
import org.springframework.stereotype.Repository;

@Repository
public  interface IngredienteRepository extends BaseJpaRepository<Ingrediente,Long>{

}