package com.higuitar.medicare.dto.request;

import com.higuitar.medicare.model.document.Prescription;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateMedicalNoteRequest(

        @NotBlank(message = "Notes is mandatory")
        String notes,
        @Valid
        List<Prescription> prescription,
        List<String>symptoms
) {
}
