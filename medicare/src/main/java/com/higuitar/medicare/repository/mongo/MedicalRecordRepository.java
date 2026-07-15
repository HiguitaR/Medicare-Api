package com.higuitar.medicare.repository.mongo;

import com.higuitar.medicare.model.document.MedicalRecord;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MedicalRecordRepository extends MongoRepository<MedicalRecord, String> {
}
