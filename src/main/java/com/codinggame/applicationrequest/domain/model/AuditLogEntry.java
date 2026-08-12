package com.codinggame.applicationrequest.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogEntry {
    private String id;
    private String requestId;
    private ApplicationRequestState previousState;
    private ApplicationRequestState newState;
    private String reason;
    private LocalDateTime changedAt;

    public static AuditLogEntry create(String requestId, ApplicationRequestState previousState, ApplicationRequestState newState, String reason) {
        AuditLogEntry entry = new AuditLogEntry();
        entry.id = java.util.UUID.randomUUID().toString();
        entry.requestId = requestId;
        entry.previousState = previousState;
        entry.newState = newState;
        entry.reason = reason;
        entry.changedAt = LocalDateTime.now();
        return entry;
    }
}
