package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.service.DoctorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;

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
