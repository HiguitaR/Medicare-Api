package com.higuitar.medicare.repository.jpa;

import com.higuitar.medicare.model.entity.Appointment;
import com.higuitar.medicare.model.entity.Doctor;
import com.higuitar.medicare.model.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    Optional<Appointment> findByDoctorAndDateTime(Doctor doctor, LocalDateTime date);
    List<Appointment> findByDoctor(Doctor doctor);
    List<Appointment> findByPatient(Patient patient);
}
