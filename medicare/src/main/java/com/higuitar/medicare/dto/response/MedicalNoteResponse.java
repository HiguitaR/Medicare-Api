package com.higuitar.medicare.dto.response;

import com.higuitar.medicare.model.document.Prescription;

import java.time.LocalDateTime;
import java.util.List;

public record MedicalNoteResponse(
        String doctorName,
        String patientName,
        String notes,
        List<Prescription> prescription,
        List<DrugResponse> drugVerification,
        List<String>symptoms,
        LocalDateTime dateTime
) {
}
