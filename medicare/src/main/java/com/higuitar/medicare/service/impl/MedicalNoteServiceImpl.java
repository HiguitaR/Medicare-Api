package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.DrugResponse;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;
import com.higuitar.medicare.integration.DrugVerificationClient;
import com.higuitar.medicare.model.document.MedicalRecord;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.repository.mongo.MedicalRecordRepository;
import com.higuitar.medicare.service.AuditLogService;
import com.higuitar.medicare.service.MedicalNoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicalNoteServiceImpl implements MedicalNoteService {

    private final AppointmentRepository appointmentRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final MedicalRecordRepository medicalRecordRepository;
    private final AuditLogService auditLogService;
    private final DrugVerificationClient drugVerificationClient;

    @Override
    public MedicalNoteResponse create(Long appointmentId, CreateMedicalNoteRequest createMedicalNoteRequest) {

        var auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        var doctor = doctorRepository.findByUser(user)
               .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

        if(!doctor.getDoctorId().equals(appointment.getDoctor().getDoctorId())) {
            throw new UnauthorizedActionException("Doctor Not assigned to this appointment");
        }

        List<DrugResponse> drugResponses = List.of();
        if(createMedicalNoteRequest.prescription() != null) {
            drugResponses = createMedicalNoteRequest.prescription().stream()
                    .map(p -> drugVerificationClient.verifyDrug(p.name()))
                    .toList();
        }

        MedicalRecord medicalRecord = new MedicalRecord();
        medicalRecord.setAppointmentId(appointmentId);
        medicalRecord.setDoctorId(doctor.getDoctorId());
        medicalRecord.setPatientId(appointment.getPatient().getPatientId());
        medicalRecord.setNotes(createMedicalNoteRequest.notes());
        medicalRecord.setSymptoms(createMedicalNoteRequest.symptoms());
        medicalRecord.setPrescriptions(createMedicalNoteRequest.prescription());

        MedicalRecord saved = medicalRecordRepository.save(medicalRecord);

        boolean allVerified = drugResponses.stream()
                .noneMatch(d -> "Medicament Verification Pending!".equals(d.activeIngredients()));
        auditLogService.logAction(
                user.getUserId(),
                "CREATE",
                "MEDICAL_NOTE",
                appointmentId,
                "Note " + saved.getId() + " created for appointment " + appointmentId
                        + " | Drugs: " + (allVerified ? "all verified" : "some pending")
        );

        return  new MedicalNoteResponse(
                doctor.getUser().getName(),
                appointment.getPatient().getUser().getName(),
                createMedicalNoteRequest.notes(),
                createMedicalNoteRequest.prescription(),
                drugResponses,
                createMedicalNoteRequest.symptoms(),
                saved.getCreatedAt()
        );
    }

    @Override
    public List<MedicalNoteResponse> findByPatientId(Long patientId) {

        var patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        List<MedicalRecord> records = medicalRecordRepository.findByPatientId(patientId);

        return records.stream()
                .map(r -> {
                    var doctor = doctorRepository.findById(r.getDoctorId())
                            .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
                    return new MedicalNoteResponse(
                            doctor.getUser().getName(),
                            patient.getUser().getName(),
                            r.getNotes(),
                            r.getPrescriptions(),
                            List.of(),
                            r.getSymptoms(),
                            r.getCreatedAt()
                    );
                }).toList();
    }
}
