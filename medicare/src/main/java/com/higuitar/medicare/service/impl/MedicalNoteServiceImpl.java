package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.service.MedicalNoteService;

import java.util.List;

public class MedicalNoteServiceImpl implements MedicalNoteService {
    @Override
    public MedicalNoteResponse create(CreateMedicalNoteRequest createMedicalNoteRequest) {
        return null;
    }

    @Override
    public List<MedicalNoteResponse> findByPatientId(Long patientId) {
        return List.of();
    }
}
