package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
