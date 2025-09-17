package users.dtos;

import users.Role;

import java.util.List;

public record UserResponse(
    Long id,
    String username,
    String email,
    Role role,
    List<String> medications,
) {
}
