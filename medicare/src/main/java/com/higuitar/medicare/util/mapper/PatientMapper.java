package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.model.entity.Patient;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PatientMapper {
    PatientResponse toPatientResponse(Patient patient);
    Patient toEntity(CreatePatientRequest request);
}
