package com.higuitar.medicare.repository.mongo;

import com.higuitar.medicare.model.document.AuditLog;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuditLogRepository extends MongoRepository<AuditLog, String> {
}
