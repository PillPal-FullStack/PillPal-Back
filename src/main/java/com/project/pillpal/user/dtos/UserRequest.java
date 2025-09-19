package com.project.pillpal.user.dtos;

import jakarta.validation.constraints.*;

public record UserRequest(
                @NotBlank(message = "Username is required") @Size(max = 20, message = "Username must be less than 20 characters") String username,

                @NotBlank(message = "Email is required") @Email(message = "Email not valid", regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$") String email,

                @NotBlank(message = "Password is required") @Size(min = 8, message = "Password must be at least 8 characters") String password) {
}
