package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.User;
import com.project.pillpal.medication.entity.Medication;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserMapperImpl implements UserMapper {
    public User dtoToEntity(UserRequest dto, List<Medication> medications) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .role(com.project.pillpal.user.entity.Role.ROLE_USER)
                .medications(new ArrayList<>(medications))
                .build();
    }

    @Override
    public UserResponse entityToDto(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                null);
    }
}
