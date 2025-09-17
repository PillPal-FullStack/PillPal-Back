package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.User;
import com.project.pillpal.medication.entity.Medication;

import java.util.List;

public interface UserMapper {
    User dtoToEntity(UserRequest dto, List<Medication> medications);
    UserResponse entityToDto(User user);
}
