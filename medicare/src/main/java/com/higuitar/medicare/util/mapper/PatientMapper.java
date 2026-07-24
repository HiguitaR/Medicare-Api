package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.model.entity.Patient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    @Mapping(source = "patientId", target = "idPatient")
    @Mapping(source = "user.userId", target = "idUser")
    PatientResponse toPatientResponse(Patient patient);
    Patient toEntity(CreatePatientRequest request);
}
