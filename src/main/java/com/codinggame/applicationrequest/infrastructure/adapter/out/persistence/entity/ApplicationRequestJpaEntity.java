package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity;

import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_application_requests")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationRequestJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationRequestState state;

    @Column
    private Long publicationId;

    @Column(columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(columnDefinition = "TEXT")
    private String deletionReason;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    @Column(nullable = false)
    private Long version;
}
