package com.higuitar.medicare.service;

/**
 * Records critical system actions for auditing purposes.
 * <p>
 * Audit entries are stored in MongoDB, keeping them decoupled from the
 * relational schema and its migration lifecycle.
 */
public interface AuditLogService {

    /**
     * Persists a single audit entry.
     *
     * @param userId     the id of the user who performed the action
     * @param action     the action performed (e.g. {@code "CREATE"}, {@code "CANCEL"})
     * @param entityType the type of entity affected (e.g. {@code "APPOINTMENT"}, {@code "MEDICAL_NOTE"})
     * @param entityId   the id of the affected entity
     * @param details    free-form details describing the action
     */
    void logAction(Long userId, String action, String entityType, Long entityId, String details);
}
