package com.higuitar.medicare.repository.mongo;

import com.higuitar.medicare.model.document.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
    List<MedicalRecord> findByPatientId(Long patientId);
}
