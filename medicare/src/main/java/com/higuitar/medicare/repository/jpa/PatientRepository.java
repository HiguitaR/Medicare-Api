package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Patient;
import com.higuitar.medicare.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Optional<Patient> findByUser(User user);
}
