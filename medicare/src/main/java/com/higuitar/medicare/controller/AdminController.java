package com.higuitar.medicare.controller;

import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.service.DoctorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final DoctorService doctorService;

    @PostMapping("/doctors")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DoctorResponse> createDoctor(@Valid @RequestBody CreateDoctorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doctorService.create(request));
    }
}
