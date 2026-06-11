package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
}
