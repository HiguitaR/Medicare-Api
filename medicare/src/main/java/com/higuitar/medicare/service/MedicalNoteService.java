package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreateMedicalNoteRequest;
import com.higuitar.medicare.dto.response.MedicalNoteResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;


import java.util.List;

/**
 * Manages clinical notes attached to appointments, stored as MongoDB documents.
 * <p>
 * Prescriptions included in a note are verified against the OpenFDA drug API.
 * When the external API is unavailable, the note is still saved and the
 * verification is reported as pending (Circuit Breaker fallback).
 */
public interface MedicalNoteService {

    /**
     * Creates a clinical note for an appointment.
     * <p>
     * Only the doctor assigned to the appointment may add notes to it.
     *
     * @param appointmentId            the appointment the note belongs to
     * @param createMedicalNoteRequest the note content: free text, symptoms and prescriptions
     * @return the created note together with the drug verification results
     * @throws ResourceNotFoundException   if the appointment or the doctor profile does not exist
     * @throws UnauthorizedActionException if the authenticated doctor is not assigned to the appointment
     */
    MedicalNoteResponse create(Long appointmentId, CreateMedicalNoteRequest createMedicalNoteRequest);

    /**
     * Retrieves the clinical history of a patient.
     *
     * @param patientId the patient id
     * @return every clinical note registered for the patient
     * @throws ResourceNotFoundException if the patient does not exist
     */
    List<MedicalNoteResponse> findByPatientId(Long patientId);
}
