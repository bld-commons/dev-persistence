package bld.commons.persistence.base.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * The Interface BaseJpaRepository.
 *
 * @param <T> the generic type
 * @param <ID> the generic type
 */
public interface BaseJpaRepository<T, ID> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T> {

}
