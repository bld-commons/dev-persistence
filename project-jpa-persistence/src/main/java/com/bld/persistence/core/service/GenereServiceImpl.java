package com.bld.persistence.core.service;

import com.bld.commons.service.JpaServiceImpl;
import com.bld.persistence.core.repository.GenereRepository;
import com.bld.persistence.core.domain.Genere;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

import com.bld.commons.processor.annotations.CustomConditionBuilder;
import com.bld.commons.processor.annotations.QueryBuilder;
import jakarta.persistence.PersistenceContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@QueryBuilder(customNativeConditions = {
		@CustomConditionBuilder(condition = "and (g.id_genere,pc.id_postazione_cucina) in (:genereTuple)", parameter = "genereTuple",keys = {"zone1","zone2"}),
@CustomConditionBuilder(condition = "and g.id_genere in (:idGenere)", parameter = "idGenere",keys = {"zone1","zone2"})
})
public  class GenereServiceImpl extends JpaServiceImpl<Genere,Long> implements GenereService{
	@Autowired
    private GenereRepository genereRepository;
    
	@PersistenceContext
    private EntityManager entityManager;
    
	@Override
    protected  JpaRepository<Genere,Long> getJpaRepository() {
        return this.genereRepository;
    }
    
	@Override
    protected  EntityManager getEntityManager() {
        return this.entityManager;
    }
    

}