package com.project.pillpal.user.dtos;

import com.project.pillpal.user.entity.User;

import java.util.ArrayList;

public class UserMapperImpl implements UserMapper {
    public User dtoToEntity(UserRequest dto) {
        return User.builder()
                .username(dto.username())
                .email(dto.email())
                .password(dto.password())
                .role(dto.role())
                .medications(new ArrayList<>(medications))
                .build();

}
