package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Doctor;
import com.higuitar.medicare.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Optional<Doctor> findByUser(User user);
}
