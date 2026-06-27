package com.higuitar.medicare.dto.response;

import com.higuitar.medicare.model.Role;

public record AuthResponse(
        String token,
        Long userId,
        String name,
        String email,
        Role role
) {
}
