package com.higuitar.medicare.dto.request;

import com.higuitar.medicare.model.document.Prescription;

import java.util.List;

public record CreateMedicalNoteRequest(
        String notes,
        List<Prescription> prescription,
        List<String>symptoms
) {
}
