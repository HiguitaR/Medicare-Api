package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.service.DoctorService;

import java.util.List;

public class DoctorServiceImpl implements DoctorService {
    @Override
    public List<DoctorResponse> findAll() {
        return List.of();
    }

    @Override
    public DoctorResponse findById(Long doctorId) {
        return null;
    }

    @Override
    public DoctorResponse create(CreateDoctorRequest request) {
        return null;
    }
}
