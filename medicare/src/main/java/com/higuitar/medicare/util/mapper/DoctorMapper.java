package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.model.entity.Doctor;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    DoctorResponse toDoctorResponse(Doctor doctor);
    Doctor toEntity(CreateDoctorRequest request);
}
