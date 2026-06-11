package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PatientRepository extends JpaRepository<Patient, Long> {
}
