package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;

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
