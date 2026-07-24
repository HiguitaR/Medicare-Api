package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.LoginRequest;
import com.higuitar.medicare.dto.request.RegisterRequest;
import com.higuitar.medicare.dto.response.AuthResponse;
import com.higuitar.medicare.exception.UserAlreadyExistException;
import com.higuitar.medicare.exception.UserNotFoundException;

/**
 * Handles user registration and authentication, issuing JWT tokens on success.
 */
public interface AuthService {

    /**
     * Registers a new patient account.
     * <p>
     * The password is stored hashed with BCrypt and the account always receives
     * the {@code PATIENT} role; privileged roles are created through the admin endpoints.
     *
     * @param request the registration data (name, email, password)
     * @return the issued JWT together with the basic user data
     * @throws UserAlreadyExistException if the email is already registered
     */
    AuthResponse register(RegisterRequest request);

    /**
     * Authenticates a user by email and password.
     *
     * @param request the login credentials
     * @return the issued JWT together with the basic user data
     * @throws UserNotFoundException                                               if no account exists for the email
     * @throws org.springframework.security.authentication.BadCredentialsException if the password does not match
     */
    AuthResponse login(LoginRequest request);
}
