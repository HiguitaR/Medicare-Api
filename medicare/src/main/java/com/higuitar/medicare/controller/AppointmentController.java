package com.higuitar.medicare.controller;


import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Appointments", description = "Medical appointment scheduling and cancellation")
public class AppointmentController {
    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Book an appointment", description = "The patient books an appointment validating doctor availability")
    @ApiResponse(responseCode = "201", description = "Appointment booked successfully")
    @ApiResponse(responseCode = "409", description = "The doctor already has an appointment at that time")
    public ResponseEntity<AppointmentResponse> createAppointment(@Valid @RequestBody CreateAppointmentRequest request){
        AppointmentResponse newAppointment = appointmentService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAppointment);
    }

    @GetMapping("/mine")
    @PreAuthorize("hasAnyRole('PATIENT', 'DOCTOR')")
    @Operation(summary = "Get my appointments", description = "Returns the appointments of the authenticated patient or doctor")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<AppointmentResponse>> findAllAppointment(){
        return ResponseEntity.ok(appointmentService.findMyAppointment());
    }

    @PatchMapping("/{id}/cancel")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Cancel an appointment", description = "Only the patient who owns the appointment, more than 24 hours in advance")
    @ApiResponse(responseCode = "200", description = "Appointment cancelled successfully")
    @ApiResponse(responseCode = "400", description = "Less than 24 hours left before the appointment")
    @ApiResponse(responseCode = "403", description = "The appointment does not belong to the authenticated patient")
    public ResponseEntity<AppointmentResponse> cancelAppointment(@PathVariable Long id){
        var cancelAppointment = appointmentService.cancel(id);
        return ResponseEntity.ok(cancelAppointment);
    }

}
