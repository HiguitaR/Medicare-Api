package com.higuitar.medicare.service;



import com.higuitar.medicare.dto.request.CreateDoctorRequest;
import com.higuitar.medicare.dto.response.DoctorResponse;
import com.higuitar.medicare.exception.ResourceNotFoundException;

import java.util.List;

/**
 * Manages doctor profiles, which extend a base user account with a specialization.
 */
public interface DoctorService {

    /**
     * Lists all registered doctors.
     *
     * @return every doctor profile in the system
     */
    List<DoctorResponse> findAll();

    /**
     * Finds a doctor by id.
     *
     * @param doctorId the doctor id
     * @return the doctor profile
     * @throws ResourceNotFoundException if no doctor exists with the given id
     */
    DoctorResponse findById(Long doctorId);

    /**
     * Creates a doctor profile linked to an existing user account.
     *
     * @param request the specialization and the id of the user to promote
     * @return the created doctor profile
     * @throws ResourceNotFoundException if the referenced user does not exist
     */
    DoctorResponse create(CreateDoctorRequest request);


}
