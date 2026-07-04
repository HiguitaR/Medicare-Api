package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.model.entity.Doctor;
import com.higuitar.medicare.repository.jpa.DoctorRepository;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.service.DoctorService;
import com.higuitar.medicare.util.mapper.DoctorMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final UserRepository userRepository;
    private final DoctorMapper doctorMapper;

    @Override
    public List<DoctorResponse> findAll() {
        return doctorRepository.findAll().stream()
                .map(doctorMapper::toDoctorResponse)
                .toList();
    }

    @Override
    public DoctorResponse findById(Long doctorId) {
        return doctorRepository.findById(doctorId)
                .map(doctorMapper::toDoctorResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
    }

    @Override
    public DoctorResponse create(CreateDoctorRequest request) {
        var user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Doctor doctor = doctorMapper.toEntity(request);
        doctor.setUser(user);

        return doctorMapper.toDoctorResponse(doctorRepository.save(doctor));
    }
}
