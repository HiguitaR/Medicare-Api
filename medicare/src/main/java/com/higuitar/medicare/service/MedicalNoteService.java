package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;


import java.util.List;

public interface MedicalNoteService {

    MedicalNoteResponse create(CreateMedicalNoteRequest createMedicalNoteRequest);
    List<MedicalNoteResponse> findByPatientId(Long patientId);
}
