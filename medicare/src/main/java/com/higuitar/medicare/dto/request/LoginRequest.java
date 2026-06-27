package com.higuitar.medicare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = "This parameter is mandatory!")
        @Email(message = "Invalid format!")
        String email,

        @NotBlank(message = "This parameter is mandatory!")
        String password
) {
}
