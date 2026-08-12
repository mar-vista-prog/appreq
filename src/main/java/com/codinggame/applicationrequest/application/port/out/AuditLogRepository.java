package com.codinggame.applicationrequest.application.port.out;

import com.codinggame.applicationrequest.domain.model.AuditLogEntry;

public interface AuditLogRepository {
    AuditLogEntry save(AuditLogEntry entry);
}
