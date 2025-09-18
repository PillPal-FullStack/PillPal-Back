package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.Role;

public record UserResponse(
        Long id,
        String username,
        String email,
        Role role,
        String token) {
}
