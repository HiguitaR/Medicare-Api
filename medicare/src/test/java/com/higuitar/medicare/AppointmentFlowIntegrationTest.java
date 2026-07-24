package com.higuitar.medicare;

import com.higuitar.medicare.dto.response.DrugResponse;
import com.higuitar.medicare.integration.DrugVerificationClient;
import com.higuitar.medicare.model.Role;
import com.higuitar.medicare.model.document.MedicalRecord;
import com.higuitar.medicare.model.entity.Patient;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.repository.mongo.AuditLogRepository;
import com.higuitar.medicare.repository.mongo.MedicalRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AppointmentFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PatientRepository patientRepository;
    @Autowired
    private DoctorRepository doctorRepository;
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private MedicalRecordRepository medicalRecordRepository;
    @MockitoBean
    private AuditLogRepository auditLogRepository;
    @MockitoBean
    private DrugVerificationClient drugVerificationClient;

    @BeforeEach
    void cleanDatabase() {
        appointmentRepository.deleteAll();
        doctorRepository.deleteAll();
        patientRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void fullFlow_registerLoginCreateDoctorBookAppointmentAddNoteAndGetRecords() throws Exception {
        // 1. Register a patient (public endpoint)
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Test Patient", "email": "patient@test.com", "password": "password1234"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("PATIENT"))
                .andReturn();
        String patientToken = readJson(registerResult).get("token").asText();
        long patientUserId = readJson(registerResult).get("userId").asLong();

        // 2. Login as the registered patient
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "patient@test.com", "password": "password1234"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // Patient profile is created directly (no public endpoint exposes it)
        User patientUser = userRepository.findById(patientUserId).orElseThrow();
        Patient patient = new Patient();
        patient.setDateOfBirth(LocalDate.of(1990, 1, 1));
        patient.setPhoneNumber("1234567890");
        patient.setUser(patientUser);
        patient = patientRepository.save(patient);

        // 3. Seed an ADMIN user and log in
        User admin = new User();
        admin.setName("Admin User");
        admin.setEmail("admin@test.com");
        admin.setPassword(passwordEncoder.encode("adminpass123"));
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
        String adminToken = loginAndGetToken("admin@test.com", "adminpass123");

        // 4. Admin creates the doctor's user account
        MvcResult doctorUserResult = mockMvc.perform(post("/api/v1/users")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Dr House", "email": "house@test.com", "password": "password1234", "role": "DOCTOR"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        long doctorUserId = readJson(doctorUserResult).get("userId").asLong();

        // 5. Admin creates the doctor profile
        MvcResult doctorResult = mockMvc.perform(post("/api/admin/doctors")
                        .header("Authorization", bearer(adminToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"specialization\": \"Cardiology\", \"userId\": " + doctorUserId + "}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.specialization").value("Cardiology"))
                .andReturn();
        long doctorId = readJson(doctorResult).get("idDoctor").asLong();

        // 6. Patient books an appointment
        MvcResult appointmentResult = mockMvc.perform(post("/api/appointments")
                        .header("Authorization", bearer(patientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doctorId\": " + doctorId + ", \"dateTime\": \"2026-12-01T10:00:00\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SCHEDULED"))
                .andReturn();
        long appointmentId = readJson(appointmentResult).get("idAppointment").asLong();

        // 7. Booking the same doctor at the same time fails with 409
        mockMvc.perform(post("/api/appointments")
                        .header("Authorization", bearer(patientToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"doctorId\": " + doctorId + ", \"dateTime\": \"2026-12-01T10:00:00\"}"))
                .andExpect(status().isConflict());

        // 8. The assigned doctor adds a clinical note (Mongo + drug verification mocked)
        when(drugVerificationClient.verifyDrug("Ibuprofen"))
                .thenReturn(new DrugResponse("Ibuprofen", "Ibuprofen"));
        when(medicalRecordRepository.save(any(MedicalRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String doctorToken = loginAndGetToken("house@test.com", "password1234");

        mockMvc.perform(post("/api/appointments/{id}/note", appointmentId)
                        .header("Authorization", bearer(doctorToken))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "notes": "Patient presents mild headache",
                                  "prescription": [{"name": "Ibuprofen", "dosage": "400mg", "frequency": "every 8 hours", "duration": "5 days"}],
                                  "symptoms": ["headache", "fatigue"]
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.doctorName").value("Dr House"))
                .andExpect(jsonPath("$.patientName").value("Test Patient"));

        // 9. Patient consults their clinical history
        MedicalRecord record = new MedicalRecord();
        record.setId("record-1");
        record.setAppointmentId(appointmentId);
        record.setDoctorId(doctorId);
        record.setPatientId(patient.getPatientId());
        record.setNotes("Patient presents mild headache");
        when(medicalRecordRepository.findByPatientId(patient.getPatientId()))
                .thenReturn(List.of(record));

        mockMvc.perform(get("/api/patients/{id}/records", patient.getPatientId())
                        .header("Authorization", bearer(patientToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].notes").value("Patient presents mild headache"));
    }

    @Test
    void protectedEndpointWithoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/appointments/mine"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpointWithInvalidToken_returns401() throws Exception {
        mockMvc.perform(get("/api/appointments/mine")
                        .header("Authorization", "Bearer not.a.valid.token"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointWithPatientToken_returns403() throws Exception {
        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Test Patient", "email": "patient@test.com", "password": "password1234"}
                                """))
                .andExpect(status().isCreated())
                .andReturn();
        String patientToken = readJson(registerResult).get("token").asText();

        mockMvc.perform(get("/api/admin/doctors")
                        .header("Authorization", bearer(patientToken)))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginWithWrongPassword_returns401() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Test Patient", "email": "patient@test.com", "password": "password1234"}
                                """))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email": "patient@test.com", "password": "wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\": \"" + email + "\", \"password\": \"" + password + "\"}"))
                .andExpect(status().isOk())
                .andReturn();
        return readJson(result).get("token").asText();
    }

    private JsonNode readJson(MvcResult result) throws Exception {
        return objectMapper.readTree(result.getResponse().getContentAsString());
    }

    private String bearer(String token) {
        return "Bearer " + token;
    }
}
