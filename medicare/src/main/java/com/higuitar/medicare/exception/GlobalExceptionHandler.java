package com.higuitar.medicare.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.URI;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(AppointmentConflictException.class)
    public ProblemDetail appointmentConflict(AppointmentConflictException ex, HttpServletRequest request) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
        response.setTitle("Appointment Conflict");
        response.setInstance(URI.create(request.getRequestURI()));
        return response;
    }

    @ExceptionHandler(LateCancellationException.class)
    public ProblemDetail lateCancellation(LateCancellationException ex, HttpServletRequest request) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        response.setTitle("Late Cancellation");
        response.setInstance(URI.create(request.getRequestURI()));
        return response;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail resourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        response.setTitle("Resource Not Found");
        response.setInstance(URI.create(request.getRequestURI()));
        return response;
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ProblemDetail unauthorizedAction(UnauthorizedActionException ex, HttpServletRequest request) {
        ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
        response.setTitle("Unauthorized");
        response.setInstance(URI.create(request.getRequestURI()));
        return response;
    }
}
