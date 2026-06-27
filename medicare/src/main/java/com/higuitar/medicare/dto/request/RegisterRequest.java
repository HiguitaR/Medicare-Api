package com.higuitar.medicare.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = "This parameter is mandatory!")
        @Size(max = 50, message = "The name must have 50 characters!")
        String name,

        @NotBlank(message = "This parameter is mandatory!")
        @Email(message = "Invalid format!")
        @Size(min = 3, max = 100, message = "Must have 100 characters!")
        String email,

        @NotBlank(message = "This parameter is mandatory!")
        @Size(min = 10, max = 100, message = "Must be between 10 and 100 characters!")
        String password
) {
}
