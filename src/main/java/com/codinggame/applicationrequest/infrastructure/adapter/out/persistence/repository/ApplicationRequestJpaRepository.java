package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.repository;

import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity.ApplicationRequestJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRequestJpaRepository extends JpaRepository<ApplicationRequestJpaEntity, String> {
    @Query("SELECT r FROM ApplicationRequestJpaEntity r WHERE UPPER(r.name) = UPPER(:name)")
    Page<ApplicationRequestJpaEntity> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT r FROM ApplicationRequestJpaEntity r WHERE r.state = :state")
    Page<ApplicationRequestJpaEntity> findByState(@Param("state") ApplicationRequestState state, Pageable pageable);

    @Query("SELECT r FROM ApplicationRequestJpaEntity r WHERE r.name = :name AND r.state = :state")
    Page<ApplicationRequestJpaEntity> findByNameAndState(
            @Param("name") String name,
            @Param("state") ApplicationRequestState state,
            Pageable pageable
    );
}
