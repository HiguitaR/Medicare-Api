package com.higuitar.medicare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateDoctorRequest(

        @NotBlank(message = "Must have a doctor specialization!")
        String specialization,

        @NotNull(message = "Must have a valid user id!")
        Long idUser
) {
}
