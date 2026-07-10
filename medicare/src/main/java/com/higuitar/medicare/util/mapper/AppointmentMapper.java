package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.model.entity.Appointment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {
    @Mapping(source = "patient.patientId", target = "idPatient")
    @Mapping(source = "doctor.doctorId", target = "idDoctor")
    AppointmentResponse toAppointmentResponse(Appointment appointment);
    Appointment toEntity(CreateAppointmentRequest request);
}
