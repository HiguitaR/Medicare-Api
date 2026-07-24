package com.higuitar.medicare.service;



import com.higuitar.medicare.dto.request.CreateAppointmentRequest;
import com.higuitar.medicare.dto.response.AppointmentResponse;
import com.higuitar.medicare.exception.AppointmentConflictException;
import com.higuitar.medicare.exception.LateCancellationException;
import com.higuitar.medicare.exception.ResourceNotFoundException;
import com.higuitar.medicare.exception.UnauthorizedActionException;

import java.util.List;

/**
 * Handles the appointment booking lifecycle: scheduling, listing and cancellation.
 * <p>
 * Core business rules enforced: no overlapping appointments for the same doctor,
 * cancellations only by the owning patient and only more than 24 hours in advance.
 */
public interface AppointmentService {

    /**
     * Lists the appointments of the currently authenticated user.
     * <p>
     * Doctors see the appointments assigned to them; patients see their own bookings.
     *
     * @return the appointments visible to the authenticated user
     * @throws ResourceNotFoundException   if the user profile cannot be resolved
     * @throws UnauthorizedActionException if the authenticated user has an unsupported role
     */
    List<AppointmentResponse> findMyAppointment();

    /**
     * Books a new appointment for the authenticated patient.
     *
     * @param request the doctor id and the requested date/time
     * @return the created appointment with status {@code SCHEDULED}
     * @throws ResourceNotFoundException    if the doctor or the patient profile does not exist
     * @throws AppointmentConflictException if the doctor already has an appointment at the requested time
     */
    AppointmentResponse create(CreateAppointmentRequest request);

    /**
     * Cancels an existing appointment.
     * <p>
     * Only the patient who owns the appointment may cancel it, and only when
     * more than 24 hours remain before the scheduled time.
     *
     * @param appointmentId the id of the appointment to cancel
     * @return the updated appointment with status {@code CANCELLED}
     * @throws ResourceNotFoundException   if the appointment does not exist
     * @throws UnauthorizedActionException if the appointment belongs to another patient
     * @throws LateCancellationException   if less than 24 hours remain before the appointment
     */
    AppointmentResponse cancel(Long appointmentId);
}
