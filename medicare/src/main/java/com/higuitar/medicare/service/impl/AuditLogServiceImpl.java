package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.model.document.AuditLog;
import com.higuitar.medicare.repository.mongo.AuditLogRepository;
import com.higuitar.medicare.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * MongoDB-backed implementation of {@link AuditLogService}; each call stores
 * one audit document describing the performed action.
 */
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {

    private final AuditLogRepository auditLogRepository;

    @Override
    public void logAction(Long userId, String action, String entityType, Long entityId, String details) {
        var log = new AuditLog();
        log.setUserId(userId);
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        log.setDetails(details);
        auditLogRepository.save(log);
    }
}
