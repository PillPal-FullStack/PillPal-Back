package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.User;

import java.util.ArrayList;
import java.util.List;

public class UserMapperImpl implements UserMapper {
    @Override
    public User dtoToEntity(UserRequest dto, List<Medication> medications) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .role(dto.role())
                .medications(new ArrayList<>(medications))
                .build();

}
