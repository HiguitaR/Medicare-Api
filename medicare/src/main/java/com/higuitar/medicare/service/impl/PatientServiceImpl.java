package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.model.entity.Patient;
import com.higuitar.medicare.repository.jpa.PatientRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.service.PatientService;
import com.higuitar.medicare.util.mapper.PatientMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JPA-backed implementation of {@link PatientService}; creating a patient only
 * links the profile to an existing user account.
 */
@Service
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PatientMapper patientMapper;

    @Override
    public List<PatientResponse> findAll() {

        return patientRepository.findAll().stream()
                .map(patientMapper::toPatientResponse)
                .toList();
    }

    @Override
    public PatientResponse findById(Long patientId) {

        return patientRepository.findById(patientId)
                .map(patientMapper::toPatientResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found."));
    }

    @Override
    public PatientResponse create(CreatePatientRequest request) {
        var user = userRepository.findById(request.UserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        Patient patient =  patientMapper.toEntity(request);
        patient.setUser(user);

        return patientMapper.toPatientResponse(patientRepository.save(patient));
    }
}
