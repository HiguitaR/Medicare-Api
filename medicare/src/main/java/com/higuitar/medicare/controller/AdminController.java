package com.higuitar.medicare.controller;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.service.DoctorService;
import com.higuitar.medicare.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Administration", description = "Doctor and patient management (ADMIN only)")
public class AdminController {

    private final DoctorService doctorService;
    private final PatientService patientService;

    @PostMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a doctor", description = "Assigns the doctor profile to an existing user")
    @ApiResponse(responseCode = "201", description = "Doctor created successfully")
    @ApiResponse(responseCode = "403", description = "Missing ADMIN role")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }

    @GetMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List doctors", description = "Returns all registered doctors")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<DoctorResponse>> findAllDoctors() {
        return ResponseEntity.ok(doctorService.findAll());
    }

    @GetMapping("/patients")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List patients", description = "Returns all registered patients")
    @ApiResponse(responseCode = "200", description = "List retrieved successfully")
    public ResponseEntity<List<PatientResponse>> findAllPatients() {
        return ResponseEntity.ok(patientService.findAll());
    }
}
