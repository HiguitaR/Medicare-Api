package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.service.PatientService;

import java.util.List;

public class PatientServiceImpl implements PatientService {
    @Override
    public List<PatientResponse> findAll() {
        return List.of();
    }

    @Override
    public PatientResponse findById(Long patientId) {
        return null;
    }

    @Override
    public PatientResponse create(CreatePatientRequest request) {
        return null;
    }
}
