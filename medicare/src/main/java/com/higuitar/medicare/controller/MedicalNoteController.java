package com.higuitar.medicare.controller;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.service.MedicalNoteService;
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
public class MedicalNoteController {
    private final MedicalNoteService medicalNoteService;

    @PostMapping("/appointments/{id}/note")
    @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<MedicalNoteResponse> createNote (@PathVariable Long id,
                                                           @Valid @RequestBody CreateMedicalNoteRequest request) {
        MedicalNoteResponse newMedicalNote = medicalNoteService.create(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newMedicalNote);
    }

    @GetMapping("/patients/{id}/records")
    @PreAuthorize("hasAnyRole('PATIENT', 'ADMIN')")
    public ResponseEntity<List<MedicalNoteResponse>> findByPatientId(@PathVariable Long id) {
        return ResponseEntity.ok(medicalNoteService.findByPatientId(id));
    }
}
