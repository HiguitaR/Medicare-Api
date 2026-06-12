package com.higuitar.medicare.dto.response;

import com.higuitar.medicare.model.AppointmentStatus;

import java.time.LocalDateTime;

public record AppointmentResponse(
        Long idAppointment,
        AppointmentStatus status,
        LocalDateTime dateTime,
        Long idPatient,
        Long idDoctor
) {
}
