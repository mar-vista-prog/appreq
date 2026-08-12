package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence;

import com.codinggame.applicationrequest.application.port.out.PublicationIdGenerator;
import com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity.PublicationIdSequenceEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PublicationIdGeneratorAdapter implements PublicationIdGenerator {

    private final EntityManager entityManager;

    @Override
    @Transactional
    public Long generatePublicationId() {
        PublicationIdSequenceEntity sequenceEntity = new PublicationIdSequenceEntity();
        entityManager.persist(sequenceEntity);
        entityManager.flush();
        return sequenceEntity.getId();
    }
}
