package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.exception.AppointmentConflictException;
import com.higuitar.medicare.exception.LateCancellationException;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;
import com.higuitar.medicare.model.entity.Appointment;
import com.higuitar.medicare.model.entity.Doctor;
import com.higuitar.medicare.model.entity.Patient;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.service.AuditLogService;
import com.higuitar.medicare.util.mapper.AppointmentMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static com.higuitar.medicare.model.AppointmentStatus.SCHEDULED;
import static com.higuitar.medicare.model.Role.ADMIN;
import static com.higuitar.medicare.model.Role.DOCTOR;
import static com.higuitar.medicare.model.Role.PATIENT;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppointmentServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private AuditLogService auditLogService;
    @InjectMocks
    private AppointmentServiceImpl appointmentService;

    private MockedStatic<SecurityContextHolder> securityMock;

    @BeforeEach
    void setUp() {
        securityMock = mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    void tearDown() {
        securityMock.close();
    }

    private void mockAuthentication(String email) {
        Authentication auth = new UsernamePasswordAuthenticationToken(email, null);
        SecurityContextImpl context = new SecurityContextImpl();
        context.setAuthentication(auth);
        securityMock.when(SecurityContextHolder::getContext).thenReturn(context);
    }

    @Test
    void findMyAppointment_Doctor_ReturnsAppointmentList() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);
        user.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setUser(user);

        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(dateTime);
        appointment.setDoctor(doctor);

        AppointmentResponse expected = new AppointmentResponse(1L, SCHEDULED, dateTime, 2L, 1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUser(user)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findByDoctor(doctor)).thenReturn(List.of(appointment));
        when(appointmentMapper.toAppointmentResponse(appointment)).thenReturn(expected);

        //when
        List<AppointmentResponse> result = appointmentService.findMyAppointment();

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected.idAppointment(), result.get(0).idAppointment());
        assertEquals(expected.status(), result.get(0).status());

        verify(userRepository).findByEmail(email);
        verify(doctorRepository).findByUser(user);
        verify(appointmentRepository).findByDoctor(doctor);
        verify(appointmentMapper).toAppointmentResponse(appointment);
    }

    @Test
    void findMyAppointment_Patient_ReturnsAppointmentList() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);
        user.setRole(PATIENT);

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(user);

        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(dateTime);
        appointment.setPatient(patient);

        AppointmentResponse expected = new AppointmentResponse(1L, SCHEDULED, dateTime, 1L, 1L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatient(patient)).thenReturn(List.of(appointment));
        when(appointmentMapper.toAppointmentResponse(appointment)).thenReturn(expected);

        //when
        List<AppointmentResponse> result = appointmentService.findMyAppointment();

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(expected.idAppointment(), result.get(0).idAppointment());

        verify(userRepository).findByEmail(email);
        verify(patientRepository).findByUser(user);
        verify(appointmentRepository).findByPatient(patient);
    }

    @Test
    void findMyAppointment_UserNotFound_ThrowsException() {
        //given
        String email = "unknown@email.com";
        mockAuthentication(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findMyAppointment());
        verify(userRepository).findByEmail(email);
        verify(doctorRepository, never()).findByUser(any());
        verify(patientRepository, never()).findByUser(any());
    }

    @Test
    void findMyAppointment_DoctorNotFound_ThrowsException() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);
        user.setRole(DOCTOR);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(doctorRepository.findByUser(user)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findMyAppointment());
        verify(doctorRepository).findByUser(user);
        verify(appointmentRepository, never()).findByDoctor(any());
    }

    @Test
    void findMyAppointment_PatientNotFound_ThrowsException() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);
        user.setRole(PATIENT);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.findByUser(user)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.findMyAppointment());
        verify(patientRepository).findByUser(user);
        verify(appointmentRepository, never()).findByPatient(any());
    }

    @Test
    void findMyAppointment_AdminRole_ThrowsException() {
        //given
        String email = "admin@email.com";
        mockAuthentication(email);

        User user = new User();
        user.setUserId(3L);
        user.setEmail(email);
        user.setRole(ADMIN);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //then
        assertThrows(UnauthorizedActionException.class, () -> appointmentService.findMyAppointment());
        verify(appointmentRepository, never()).findByDoctor(any());
        verify(appointmentRepository, never()).findByPatient(any());
    }

    // ==================== CREATE TESTS ====================

    @Test
    void create_Appointment_Success() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);
        user.setRole(PATIENT);

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(user);

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setName("Dr. Smith");

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setUser(doctorUser);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(dateTime);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        AppointmentResponse expected = new AppointmentResponse(1L, SCHEDULED, dateTime, 1L, doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByDoctorAndDateTime(doctor, dateTime)).thenReturn(Optional.empty());
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);
        when(appointmentMapper.toAppointmentResponse(appointment)).thenReturn(expected);

        //when
        AppointmentResponse result = appointmentService.create(request);

        //then
        assertNotNull(result);
        assertEquals(expected.idAppointment(), result.idAppointment());
        assertEquals(expected.status(), result.status());
        assertEquals(expected.dateTime(), result.dateTime());

        verify(doctorRepository).findById(doctorId);
        verify(userRepository).findByEmail(email);
        verify(patientRepository).findByUser(user);
        verify(appointmentRepository).findByDoctorAndDateTime(doctor, dateTime);
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void create_DoctorNotFound_ThrowsException() {
        //given
        Long doctorId = 99L;
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
        verify(userRepository, never()).findByEmail(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_UserNotFound_ThrowsException() {
        //given
        String email = "unknown@email.com";
        mockAuthentication(email);

        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
        verify(patientRepository, never()).findByUser(any());
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_PatientNotFound_ThrowsException() {
        //given
        String email = "notpatient@email.com";
        mockAuthentication(email);

        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime);

        User user = new User();
        user.setUserId(1L);
        user.setEmail(email);
        user.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.findByUser(user)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void create_AppointmentConflict_ThrowsException() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        Long doctorId = 1L;
        LocalDateTime dateTime = LocalDateTime.of(2026, 8, 1, 10, 0);
        CreateAppointmentRequest request = new CreateAppointmentRequest(doctorId, dateTime);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);
        user.setRole(PATIENT);

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(user);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);

        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentId(99L);

        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(patientRepository.findByUser(user)).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByDoctorAndDateTime(doctor, dateTime)).thenReturn(Optional.of(existingAppointment));

        //then
        assertThrows(AppointmentConflictException.class, () -> appointmentService.create(request));
        verify(appointmentRepository, never()).save(any());
    }

    // ==================== CANCEL TESTS ====================

    @Test
    void cancel_Appointment_Success() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;
        LocalDateTime futureDateTime = LocalDateTime.now().plusDays(2);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(user);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(futureDateTime);
        appointment.setPatient(patient);

        AppointmentResponse expected = new AppointmentResponse(appointmentId, 
                com.higuitar.medicare.model.AppointmentStatus.CANCELLED, futureDateTime, 1L, 1L);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(appointmentRepository.save(appointment)).thenReturn(appointment);
        when(appointmentMapper.toAppointmentResponse(appointment)).thenReturn(expected);

        //when
        AppointmentResponse result = appointmentService.cancel(appointmentId);

        //then
        assertNotNull(result);
        assertEquals(com.higuitar.medicare.model.AppointmentStatus.CANCELLED, result.status());
        verify(appointmentRepository).findById(appointmentId);
        verify(appointmentRepository).save(appointment);
    }

    @Test
    void cancel_AppointmentNotFound_ThrowsException() {
        //given
        Long appointmentId = 99L;

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class, () -> appointmentService.cancel(appointmentId));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancel_UnauthorizedUser_ThrowsException() {
        //given
        String email = "other@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;

        User otherUser = new User();
        otherUser.setUserId(3L);
        otherUser.setEmail(email);

        User ownerUser = new User();
        ownerUser.setUserId(2L);
        ownerUser.setEmail("owner@email.com");

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(ownerUser);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setDateTime(LocalDateTime.now().plusDays(2));
        appointment.setPatient(patient);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(otherUser));

        //then
        assertThrows(UnauthorizedActionException.class, () -> appointmentService.cancel(appointmentId));
        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void cancel_LateCancellation_ThrowsException() {
        //given
        String email = "patient@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;
        LocalDateTime soonDateTime = LocalDateTime.now().plusHours(12);

        User user = new User();
        user.setUserId(2L);
        user.setEmail(email);

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(user);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setStatus(SCHEDULED);
        appointment.setDateTime(soonDateTime);
        appointment.setPatient(patient);

        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        //then
        assertThrows(LateCancellationException.class, () -> appointmentService.cancel(appointmentId));
        verify(appointmentRepository, never()).save(any());
    }
}
