package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.service.AppointmentService;

import java.util.List;

public class AppointmentServiceImpl implements AppointmentService {
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
