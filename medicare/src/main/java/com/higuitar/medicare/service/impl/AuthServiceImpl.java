package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.LoginRequest;
import com.higuitar.medicare.dto.request.RegisterRequest;
import com.higuitar.medicare.dto.response.AuthResponse;
import com.higuitar.medicare.exception.UserAlreadyExistException;
import com.higuitar.medicare.exception.UserNotFoundException;
import com.higuitar.medicare.model.Role;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.security.JwtService;
import com.higuitar.medicare.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistException("User already exists with email: " + request.email());
        }

        var user = new User();
        user.setName(request.name());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.PATIENT);
        user = userRepository.save(user);

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + request.email()));

        var userDetails = userDetailsService.loadUserByUsername(user.getEmail());
        var token = jwtService.generateToken(userDetails);

        return new AuthResponse(token, user.getUserId(), user.getName(), user.getEmail(), user.getRole());
    }
}
