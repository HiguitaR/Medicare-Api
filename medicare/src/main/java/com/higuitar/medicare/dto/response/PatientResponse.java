package com.higuitar.medicare.dto.response;

import java.time.LocalDate;

public record PatientResponse(
        Long idPatient,
        LocalDate dateOfBirth,
        String phoneNumber,
        Long idUser
) {
}
