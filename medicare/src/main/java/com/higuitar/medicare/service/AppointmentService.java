package com.higuitar.medicare.service;



import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;

import java.util.List;

public interface AppointmentService {

    List<AppointmentResponse> findMyAppointment();
    AppointmentResponse create(CreateAppointmentRequest request);
    AppointmentResponse cancel(Long appointmentId);
}
