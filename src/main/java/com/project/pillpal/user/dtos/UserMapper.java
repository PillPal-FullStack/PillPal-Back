package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.User;

public interface UserMapper {
    User dtoToEntity(UserRequest dto, List<Medication> medications);
    UserResponse entityToDto(User user);
}
