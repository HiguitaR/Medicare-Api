package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;


import java.util.List;

public interface PatientService {
    List<PatientResponse> findAll();
    PatientResponse findById(Long patientId);
    PatientResponse create(CreatePatientRequest request);

}
