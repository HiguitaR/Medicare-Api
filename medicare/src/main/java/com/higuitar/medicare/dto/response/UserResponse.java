package com.higuitar.medicare.dto.response;

import com.higuitar.medicare.model.Role;

public record UserResponse(
        Long idUser,
        String name,
        String email,
        Role role
) {
}
