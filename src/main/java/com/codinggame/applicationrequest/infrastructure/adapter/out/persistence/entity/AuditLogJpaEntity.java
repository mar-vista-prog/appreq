package com.codinggame.applicationrequest.infrastructure.adapter.out.persistence.entity;

import com.codinggame.applicationrequest.domain.model.ApplicationRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "t_audit_logs")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogJpaEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column
    private ApplicationRequestState previousState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationRequestState newState;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}
