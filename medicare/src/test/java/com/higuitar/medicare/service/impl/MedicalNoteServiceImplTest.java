package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.DrugResponse;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;
import com.higuitar.medicare.integration.DrugVerificationClient;
import com.higuitar.medicare.model.document.MedicalRecord;
import com.higuitar.medicare.model.document.Prescription;
import com.higuitar.medicare.model.entity.Appointment;
import com.higuitar.medicare.model.entity.Doctor;
import com.higuitar.medicare.model.entity.Patient;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.repository.mongo.MedicalRecordRepository;
import com.higuitar.medicare.service.AuditLogService;
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

import static com.higuitar.medicare.model.Role.DOCTOR;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MedicalNoteServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private MedicalRecordRepository medicalRecordRepository;
    @Mock
    private AuditLogService auditLogService;
    @Mock
    private DrugVerificationClient drugVerificationClient;
    @InjectMocks
    private MedicalNoteServiceImpl medicalNoteService;

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

    // ==================== CREATE TESTS ====================

    @Test
    void create_Success() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;
        Long doctorId = 1L;

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setEmail(email);
        doctorUser.setName("Dr. Smith");
        doctorUser.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setUser(doctorUser);

        User patientUser = new User();
        patientUser.setUserId(2L);
        patientUser.setName("John Doe");

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(patientUser);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        Prescription prescription = new Prescription("Ibuprofen", "200mg", "2x/day", "7 days");
        DrugResponse drugResponse = new DrugResponse("Advil", "Ibuprofen");
        CreateMedicalNoteRequest request = new CreateMedicalNoteRequest(
                "Headache",
                List.of(prescription),
                List.of("fever", "pain")
        );

        MedicalRecord savedRecord = new MedicalRecord();
        savedRecord.setId("mongo123");
        savedRecord.setAppointmentId(appointmentId);
        savedRecord.setDoctorId(doctorId);
        savedRecord.setPatientId(patient.getPatientId());
        savedRecord.setNotes("Headache");
        savedRecord.setSymptoms(List.of("fever", "pain"));
        savedRecord.setPrescriptions(List.of(prescription));
        LocalDateTime savedTime = LocalDateTime.of(2026, 7, 18, 10, 0);
        savedRecord.setCreatedAt(savedTime);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(drugVerificationClient.verifyDrug("Ibuprofen")).thenReturn(drugResponse);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        //when
        MedicalNoteResponse result = medicalNoteService.create(appointmentId, request);

        //then
        assertNotNull(result);
        assertEquals("Dr. Smith", result.doctorName());
        assertEquals("John Doe", result.patientName());
        assertEquals("Headache", result.notes());
        assertEquals(1, result.drugVerification().size());
        assertEquals("Advil", result.drugVerification().get(0).brandName());
        assertEquals("Ibuprofen", result.drugVerification().get(0).activeIngredients());
        assertEquals(List.of("fever", "pain"), result.symptoms());
        assertEquals(savedTime, result.dateTime());

        verify(userRepository).findByEmail(email);
        verify(doctorRepository).findByUser(doctorUser);
        verify(appointmentRepository).findById(appointmentId);
        verify(drugVerificationClient).verifyDrug("Ibuprofen");
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(auditLogService).logAction(eq(doctorUser.getUserId()), eq("CREATE"), eq("MEDICAL_NOTE"), eq(appointmentId), anyString());
    }

    @Test
    void create_Success_NoPrescriptions() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setEmail(email);
        doctorUser.setName("Dr. Smith");
        doctorUser.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setUser(doctorUser);

        User patientUser = new User();
        patientUser.setUserId(2L);
        patientUser.setName("John Doe");

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(patientUser);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        CreateMedicalNoteRequest request = new CreateMedicalNoteRequest(
                "Routine checkup",
                null,
                List.of("cough")
        );

        MedicalRecord savedRecord = new MedicalRecord();
        savedRecord.setId("mongo456");
        savedRecord.setAppointmentId(appointmentId);
        savedRecord.setDoctorId(doctor.getDoctorId());
        savedRecord.setPatientId(patient.getPatientId());
        savedRecord.setNotes("Routine checkup");
        savedRecord.setSymptoms(List.of("cough"));
        savedRecord.setPrescriptions(null);
        savedRecord.setCreatedAt(LocalDateTime.of(2026, 7, 18, 11, 0));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        //when
        MedicalNoteResponse result = medicalNoteService.create(appointmentId, request);

        //then
        assertNotNull(result);
        assertEquals("Routine checkup", result.notes());
        assertNull(result.prescription());
        assertTrue(result.drugVerification().isEmpty());

        verify(drugVerificationClient, never()).verifyDrug(anyString());
        verify(medicalRecordRepository).save(any(MedicalRecord.class));
    }

    @Test
    void create_AppointmentNotFound_ThrowsException() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        Long appointmentId = 99L;

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setEmail(email);
        doctorUser.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setUser(doctorUser);

        CreateMedicalNoteRequest request = new CreateMedicalNoteRequest(
                "Headache", null, null
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class,
                () -> medicalNoteService.create(appointmentId, request));
        verify(medicalRecordRepository, never()).save(any());
    }

    @Test
    void create_DoctorNotAssigned_ThrowsException() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;

        User authenticatedUser = new User();
        authenticatedUser.setUserId(1L);
        authenticatedUser.setEmail(email);
        authenticatedUser.setRole(DOCTOR);

        Doctor authenticatedDoctor = new Doctor();
        authenticatedDoctor.setDoctorId(1L);
        authenticatedDoctor.setUser(authenticatedUser);

        Doctor assignedDoctor = new Doctor();
        assignedDoctor.setDoctorId(2L);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setDoctor(assignedDoctor);

        CreateMedicalNoteRequest request = new CreateMedicalNoteRequest(
                "Headache", null, null
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(authenticatedUser));
        when(doctorRepository.findByUser(authenticatedUser)).thenReturn(Optional.of(authenticatedDoctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

        //then
        assertThrows(UnauthorizedActionException.class,
                () -> medicalNoteService.create(appointmentId, request));
        verify(medicalRecordRepository, never()).save(any());
        verify(drugVerificationClient, never()).verifyDrug(anyString());
    }

    @Test
    void create_DrugVerificationFallback() {
        //given
        String email = "doctor@email.com";
        mockAuthentication(email);

        Long appointmentId = 1L;

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setEmail(email);
        doctorUser.setName("Dr. Smith");
        doctorUser.setRole(DOCTOR);

        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);
        doctor.setUser(doctorUser);

        User patientUser = new User();
        patientUser.setUserId(2L);
        patientUser.setName("John Doe");

        Patient patient = new Patient();
        patient.setPatientId(1L);
        patient.setUser(patientUser);

        Appointment appointment = new Appointment();
        appointment.setAppointmentId(appointmentId);
        appointment.setDoctor(doctor);
        appointment.setPatient(patient);

        Prescription prescription = new Prescription("UnknownDrug", "100mg", "1x/day", "5 days");
        DrugResponse fallbackResponse = new DrugResponse("UnknownDrug", "Medicament Verification Pending!");
        CreateMedicalNoteRequest request = new CreateMedicalNoteRequest(
                "Headache",
                List.of(prescription),
                List.of("fever")
        );

        MedicalRecord savedRecord = new MedicalRecord();
        savedRecord.setId("mongo789");
        savedRecord.setAppointmentId(appointmentId);
        savedRecord.setDoctorId(doctor.getDoctorId());
        savedRecord.setPatientId(patient.getPatientId());
        savedRecord.setNotes("Headache");
        savedRecord.setSymptoms(List.of("fever"));
        savedRecord.setPrescriptions(List.of(prescription));
        savedRecord.setCreatedAt(LocalDateTime.of(2026, 7, 18, 12, 0));

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(doctorUser));
        when(doctorRepository.findByUser(doctorUser)).thenReturn(Optional.of(doctor));
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
        when(drugVerificationClient.verifyDrug("UnknownDrug")).thenReturn(fallbackResponse);
        when(medicalRecordRepository.save(any(MedicalRecord.class))).thenReturn(savedRecord);

        //when
        MedicalNoteResponse result = medicalNoteService.create(appointmentId, request);

        //then
        assertNotNull(result);
        assertEquals("Dr. Smith", result.doctorName());
        assertEquals("John Doe", result.patientName());
        assertEquals(1, result.drugVerification().size());
        assertEquals("UnknownDrug", result.drugVerification().get(0).brandName());
        assertEquals("Medicament Verification Pending!", result.drugVerification().get(0).activeIngredients());

        verify(medicalRecordRepository).save(any(MedicalRecord.class));
        verify(auditLogService).logAction(
                eq(doctorUser.getUserId()),
                eq("CREATE"),
                eq("MEDICAL_NOTE"),
                eq(appointmentId),
                contains("some pending")
        );
    }

    // ==================== findByPatientId TESTS ====================

    @Test
    void findByPatientId_Success() {
        //given
        Long patientId = 1L;
        Long doctorId = 1L;

        User patientUser = new User();
        patientUser.setUserId(2L);
        patientUser.setName("John Doe");

        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setUser(patientUser);

        User doctorUser = new User();
        doctorUser.setUserId(1L);
        doctorUser.setName("Dr. Smith");

        Doctor doctor = new Doctor();
        doctor.setDoctorId(doctorId);
        doctor.setUser(doctorUser);

        Prescription prescription = new Prescription("Ibuprofen", "200mg", "2x/day", "7 days");

        MedicalRecord record = new MedicalRecord();
        record.setId("mongo123");
        record.setAppointmentId(1L);
        record.setDoctorId(doctorId);
        record.setPatientId(patientId);
        record.setNotes("Headache");
        record.setSymptoms(List.of("fever"));
        record.setPrescriptions(List.of(prescription));
        LocalDateTime recordTime = LocalDateTime.of(2026, 7, 18, 10, 0);
        record.setCreatedAt(recordTime);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatientId(patientId)).thenReturn(List.of(record));
        when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));

        //when
        List<MedicalNoteResponse> result = medicalNoteService.findByPatientId(patientId);

        //then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Dr. Smith", result.get(0).doctorName());
        assertEquals("John Doe", result.get(0).patientName());
        assertEquals("Headache", result.get(0).notes());
        assertEquals(1, result.get(0).prescription().size());
        assertEquals("Ibuprofen", result.get(0).prescription().get(0).name());
        assertTrue(result.get(0).drugVerification().isEmpty());
        assertEquals(List.of("fever"), result.get(0).symptoms());
        assertEquals(recordTime, result.get(0).dateTime());

        verify(patientRepository).findById(patientId);
        verify(medicalRecordRepository).findByPatientId(patientId);
        verify(doctorRepository).findById(doctorId);
    }

    @Test
    void findByPatientId_EmptyRecords() {
        //given
        Long patientId = 1L;

        User patientUser = new User();
        patientUser.setUserId(2L);
        patientUser.setName("John Doe");

        Patient patient = new Patient();
        patient.setPatientId(patientId);
        patient.setUser(patientUser);

        when(patientRepository.findById(patientId)).thenReturn(Optional.of(patient));
        when(medicalRecordRepository.findByPatientId(patientId)).thenReturn(List.of());

        //when
        List<MedicalNoteResponse> result = medicalNoteService.findByPatientId(patientId);

        //then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(patientRepository).findById(patientId);
        verify(medicalRecordRepository).findByPatientId(patientId);
        verify(doctorRepository, never()).findById(any());
    }

    @Test
    void findByPatientId_PatientNotFound_ThrowsException() {
        //given
        Long patientId = 99L;

        when(patientRepository.findById(patientId)).thenReturn(Optional.empty());

        //then
        assertThrows(ResourceNotFoundException.class,
                () -> medicalNoteService.findByPatientId(patientId));
        verify(medicalRecordRepository, never()).findByPatientId(any());
        verify(doctorRepository, never()).findById(any());
    }
}
