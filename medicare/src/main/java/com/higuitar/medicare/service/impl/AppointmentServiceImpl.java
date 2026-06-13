package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.repository.jpa.AppointmentRepository;
import com.higuitar.medicare.service.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final AppointmentRepository appointmentRepository;

    @Override
    public List<AppointmentResponse> findMyAppointment(Long userId) {
        return List.of();
    }

    @Override
    public AppointmentResponse create(CreateAppointmentRequest request) {
        return null;
    }

    @Override
    public AppointmentResponse cancel(Long appointmentId) {
        return null;
    }
}
