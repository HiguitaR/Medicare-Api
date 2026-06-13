package com.higuitar.medicare.service;



import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;

import java.util.List;

public interface DoctorService {

    List<DoctorResponse> findAll();
    DoctorResponse findById(Long doctorId);
    DoctorResponse create(CreateDoctorRequest request);


}
