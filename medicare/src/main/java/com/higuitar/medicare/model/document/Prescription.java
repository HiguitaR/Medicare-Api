package com.higuitar.medicare.model.document;

public record Prescription(
        String name,
        String dosage,
        String frequency,
        String duration
) {
}
