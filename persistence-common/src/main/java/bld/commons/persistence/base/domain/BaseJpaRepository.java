package bld.commons.persistence.base.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}
