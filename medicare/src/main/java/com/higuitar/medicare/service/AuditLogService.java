package com.higuitar.medicare.service;

public interface AuditLogService {
    void logAction(Long userId, String action, String entityType, Long entityId, String details);
}
