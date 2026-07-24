package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreatePatientRequest;
import com.higuitar.medicare.dto.response.PatientResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;


import java.util.List;

/**
 * Manages patient profiles, which extend a base user account with
 * date of birth and contact information.
 */
public interface PatientService {

    /**
     * Lists all registered patients.
     *
     * @return every patient profile in the system
     */
    List<PatientResponse> findAll();

    /**
     * Finds a patient by id.
     *
     * @param patientId the patient id
     * @return the patient profile
     * @throws ResourceNotFoundException if no patient exists with the given id
     */
    PatientResponse findById(Long patientId);

    /**
     * Creates a patient profile linked to an existing user account.
     *
     * @param request the date of birth, phone number and the id of the user to link
     * @return the created patient profile
     * @throws ResourceNotFoundException if the referenced user does not exist
     */
    PatientResponse create(CreatePatientRequest request);

}
