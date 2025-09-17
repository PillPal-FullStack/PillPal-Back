package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.Role;

import java.util.List;

public record UserResponse(
    Long id,
    String username,
    String email,
    Role role,
    List<String> medications,
) {
}
