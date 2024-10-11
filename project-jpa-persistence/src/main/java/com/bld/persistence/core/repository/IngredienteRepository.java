package com.bld.persistence.core.repository;

import com.bld.persistence.core.domain.Ingrediente;
import com.bld.commons.repository.BaseJpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public  interface IngredienteRepository extends BaseJpaRepository<Ingrediente,Long>{

}