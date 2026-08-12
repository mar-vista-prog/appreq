package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.repository;

import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, String> {
    List<AuditLogJpaEntity> findByRequestId(String requestId);
}
