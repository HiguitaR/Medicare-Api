package com.higuitar.medicare.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Document("audit_logs")
public class AuditLog {
    @Id
    private String id;
    private Long userId;
    private String action;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime date = LocalDateTime.now();
}
