package com.higuitar.medicare.dto.response;

import com.higuitar.medicare.model.Role;

public record UserResponse(
        Long userId,
        String name,
        String email,
        Role role
) {
}
