package com.higuitar.medicare.model.document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Document("medical_records")
public class MedicalRecord {
    @Id
    private String id;
    private Long appointmentId;
    private Long doctorId;
    private Long patientId;
    private String notes;
    private List<String> symptoms;
    private List<Prescription> prescriptions;
    private LocalDateTime createdAt = LocalDateTime.now();
}
