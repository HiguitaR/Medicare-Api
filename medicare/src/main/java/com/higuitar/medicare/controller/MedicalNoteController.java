package com.higuitar.medicare.controller;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.service.MedicalNoteService;
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
@RequestMapping("/api")
@Tag(name = "Medical Notes", description = "Clinical notes and patient history (MongoDB)")
public class MedicalNoteController {
    private final MedicalNoteService medicalNoteService;

    @PostMapping("/appointments/{id}/note")
    @PreAuthorize("hasRole('DOCTOR')")
    @Operation(summary = "Add a clinical note", description = "Only the doctor assigned to the appointment. Verifies drugs against OpenFDA with Circuit Breaker")
    @ApiResponse(responseCode = "201", description = "Clinical note created successfully")
    @ApiResponse(responseCode = "403", description = "The doctor is not assigned to this appointment")
    public ResponseEntity<MedicalNoteResponse> createNote (@PathVariable Long id,
                                                           @Valid @RequestBody CreateMedicalNoteRequest request) {
        MedicalNoteResponse newMedicalNote = medicalNoteService.create(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMedicalNote);
    }

    @GetMapping("/patients/{id}/records")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    @Operation(summary = "Get clinical history", description = "Returns the patient's clinical notes")
    @ApiResponse(responseCode = "200", description = "History retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Patient not found")
    public ResponseEntity<List<MedicalNoteResponse>> findByPatientId(@PathVariable Long id) {
        return ResponseEntity.ok(medicalNoteService.findByPatientId(id));
    }
}
