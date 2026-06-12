package com.higuitar.medicare.dto.response;

public record DoctorResponse(
        Long idDoctor,
        String specialization,
        Long idUser
) {
}
