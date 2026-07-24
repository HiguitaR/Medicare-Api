package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.model.entity.Doctor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DoctorMapper {
    @Mapping(source = "doctorId", target = "idDoctor")
    @Mapping(source = "user.userId", target = "idUser")
    DoctorResponse toDoctorResponse(Doctor doctor);
    Doctor toEntity(CreateDoctorRequest request);
}
