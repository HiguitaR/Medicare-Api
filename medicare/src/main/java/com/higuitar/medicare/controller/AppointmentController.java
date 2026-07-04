package com.higuitar.medicare.controller;


import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request){
        AppointmentResponse newAppointment = appointmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    public ResponseEntity<List<AppointmentResponse>> findAllAppointment(){
        return ResponseEntity.ok(appointmentService.findMyAppointment());
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id){
        var cancelAppointment = appointmentService.cancel(id);
        return ResponseEntity.ok(cancelAppointment);
    }

}
