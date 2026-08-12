package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence;

import com.codinggame.applicationrequest.domain.model.AuditLogEntry;
import com.codinggame.applicationrequest.application.port.out.AuditLogRepository;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity.AuditLogJpaEntity;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.repository.AuditLogJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuditLogRepositoryAdapter implements AuditLogRepository {

    private final AuditLogJpaRepository jpaRepository;

    @Override
    public AuditLogEntry save(AuditLogEntry entry) {
        AuditLogJpaEntity entity = toDomainEntity(entry);
        AuditLogJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    private AuditLogEntry toDomain(AuditLogJpaEntity entity) {
        return new AuditLogEntry(
                entity.getId(),
                entity.getRequestId(),
                entity.getPreviousState(),
                entity.getNewState(),
                entity.getReason(),
                entity.getChangedAt()
        );
    }

    private AuditLogJpaEntity toDomainEntity(AuditLogEntry entry) {
        return new AuditLogJpaEntity(
                entry.getId(),
                entry.getRequestId(),
                entry.getPreviousState(),
                entry.getNewState(),
                entry.getReason(),
                entry.getChangedAt()
        );
    }
}
