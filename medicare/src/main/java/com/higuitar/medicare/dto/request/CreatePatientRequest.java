package com.higuitar.medicare.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreatePatientRequest(

        @NotNull(message = "Must have user id!")
        Long idUser,

        @NotNull(message = "Must have a valid date format yyyy-mm-dd")
        LocalDate dateOfBirth,

        @NotBlank(message = "Must have a phone number!")
        @Size(min = 7, max = 15, message = "Must have a valid phone number format until 7 - 15 characters")
        String phoneNumber
) {
}
