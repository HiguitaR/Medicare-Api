package com.higuitar.medicare.model.document;

import jakarta.validation.constraints.NotBlank;

public record Prescription(

        @NotBlank(message = "Drug name is mandatory")
        String name,
        @NotBlank(message = "Dosage is mandatory")
        String dosage,
        @NotBlank(message = "Frequency is mandatory")
        String frequency,
        @NotBlank(message = "Duration is mandatory")
        String duration
) {
}
