package com.higuitar.medicare.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record CreateAppointmentRequest(

        @NotNull(message = "Must have a valid doctor id!")
        Long idDoctor,

        @NotNull(message = "Must have a valid date time format dd/mm/yyyy:time")
        LocalDateTime dateTime
) {
}
