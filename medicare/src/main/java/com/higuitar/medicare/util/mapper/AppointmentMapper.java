package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.model.entity.Appointment;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    AppointmentResponse toAppointmentResponse(Appointment appointment);
    Appointment toEntity(CreateAppointmentRequest request);
}
