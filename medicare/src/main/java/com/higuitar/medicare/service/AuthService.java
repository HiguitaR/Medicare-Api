package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.LoginRequest;
import com.higuitar.medicare.dto.request.RegisterRequest;
import com.higuitar.medicare.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
