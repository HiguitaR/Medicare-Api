package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.exception.AppointmentConflictException;
import com.higuitar.medicare.exception.LateCancellationException;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;
import com.higuitar.medicare.model.Role;
import com.higuitar.medicare.model.entity.Appointment;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.service.AppointmentService;
import com.higuitar.medicare.util.mapper.AppointmentMapper;
import com.higuitar.medicare.util.mapper.DoctorMapper;
import com.higuitar.medicare.util.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import static com.higuitar.medicare.model.AppointmentStatus.CANCELLED;
import static com.higuitar.medicare.model.AppointmentStatus.SCHEDULED;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final AppointmentMapper appointmentMapper;
    @Override
    public List<AppointmentResponse> findMyAppointment() {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if(user.getRole() == Role.DOCTOR) {
            var doctor = doctorRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

            return appointmentRepository.findByDoctor(doctor).stream()
                    .map(appointmentMapper::toAppointmentResponse)
                    .toList();
        }else if(user.getRole() == Role.PATIENT) {
            var patient = patientRepository.findByUser(user)
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

            return appointmentRepository.findByPatient(patient).stream()
                    .map(appointmentMapper::toAppointmentResponse)
                    .toList();
        }else{
            throw new UnauthorizedActionException("Unauthorized Action");
        }

    }

    @Override
    public AppointmentResponse create(CreateAppointmentRequest request) {

        var doctor = doctorRepository.findById(request.doctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var patient = patientRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        if(appointmentRepository.findByDoctorAndDateTime(doctor, request.dateTime()).isPresent()) {
            throw new AppointmentConflictException("Appointment already exists");
        }

        Appointment appointment = new Appointment();
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(request.dateTime());
        Appointment saved = appointmentRepository.save(appointment);

        return new AppointmentResponse(
                saved.getAppointmentId(),
                saved.getStatus(),
                saved.getDateTime(),
                saved.getPatient().getPatientId(),
                saved.getDoctor().getDoctorId()
        );

    }

    @Override
    public AppointmentResponse cancel(Long appointmentId) {

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if (!Objects.equals(auth.getName(), appointment.getPatient().getUser().getEmail())) {
            throw new UnauthorizedActionException("Not your appointment");
        }

        if (appointment.getDateTime().isBefore(LocalDateTime.now().plusHours(24))) {
            throw new LateCancellationException("Cannot cancel less than 24 hours before");
        }

        appointment.setStatus(CANCELLED);
        Appointment saved = appointmentRepository.save(appointment);
        return new AppointmentResponse(
                saved.getAppointmentId(),
                saved.getStatus(),
                saved.getDateTime(), saved.getPatient().getPatientId(),
                saved.getDoctor().getDoctorId()
        );
    }
}
