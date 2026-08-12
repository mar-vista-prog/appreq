package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence;

import com.codinggame.applicationrequest.domain.exception.ConcurrentModificationException;
import com.codinggame.applicationrequest.domain.model.ApplicationRequest;
import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import com.codinggame.applicationrequest.application.port.out.ApplicationRequestRepository;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity.ApplicationRequestJpaEntity;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.repository.ApplicationRequestJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.jpa.JpaOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ApplicationRequestRepositoryAdapter implements ApplicationRequestRepository {

    private final ApplicationRequestJpaRepository jpaRepository;

    @Override
    public ApplicationRequest save(ApplicationRequest request) {
        try {
            ApplicationRequestJpaEntity entity = toDomainEntity(request);
            ApplicationRequestJpaEntity saved = jpaRepository.save(entity);
            return toDomain(saved);
        } catch (JpaOptimisticLockingFailureException e) {
            throw new ConcurrentModificationException(
                    "Request was modified concurrently. Please refresh and retry.",
                    e
            );
        }
    }

    @Override
    public Optional<ApplicationRequest> findById(String id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Page<ApplicationRequest> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ApplicationRequestJpaEntity> result = jpaRepository.findAll(pageable);
        return toPage(result);
    }

    @Override
    public Page<ApplicationRequest> findByName(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ApplicationRequestJpaEntity> result = jpaRepository.findByName(name, pageable);
        return toPage(result);
    }

    @Override
    public Page<ApplicationRequest> findByState(ApplicationRequestState state, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ApplicationRequestJpaEntity> result = jpaRepository.findByState(state, pageable);
        return toPage(result);
    }

    @Override
    public Page<ApplicationRequest> findByNameAndState(String name, ApplicationRequestState state, int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ApplicationRequestJpaEntity> result = jpaRepository.findByNameAndState(name, state, pageable);
        return toPage(result);
    }

    private Page<ApplicationRequest> toPage(Page<ApplicationRequestJpaEntity> page) {
        return new PageImpl<>(
                page.getContent().stream().map(this::toDomain).toList(),
                PageRequest.of(page.getNumber(), page.getSize()),
                page.getTotalElements()
        );
    }

    private ApplicationRequest toDomain(ApplicationRequestJpaEntity entity) {
        return new ApplicationRequest(
                entity.getId(),
                entity.getName(),
                entity.getBody(),
                entity.getState(),
                entity.getPublicationId(),
                entity.getRejectionReason(),
                entity.getDeletionReason(),
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getVersion()
        );
    }

    private ApplicationRequestJpaEntity toDomainEntity(ApplicationRequest request) {
        return new ApplicationRequestJpaEntity(
                request.getId(),
                request.getName(),
                request.getBody(),
                request.getState(),
                request.getPublicationId(),
                request.getRejectionReason(),
                request.getDeletionReason(),
                request.getCreatedAt(),
                request.getUpdatedAt(),
                request.getVersion() != null ? request.getVersion() : 0L
        );
    }
}
