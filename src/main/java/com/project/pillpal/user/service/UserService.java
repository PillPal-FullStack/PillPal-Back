package com.project.pillpal.user.service;

import com.project.pillpal.user.dtos.UserMapperImpl;
import com.project.pillpal.user.dtos.UserRequest;
import com.project.pillpal.user.dtos.UserResponse;
import com.project.pillpal.exceptions.EntityNotFoundException;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;

    public User findByUsername(String username) throws EntityNotFoundException {
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), username));
    }

    public User findById(Long id) throws EntityNotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), id.toString()));
    }

    @Transactional
    public UserResponse registerUser(UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        User user = userMapperImpl.dtoToEntity(request, new ArrayList<>());

        if (user.getRole() == null) {
            user.setRole(Role.ROLE_USER);
        }

        // For simplicity, store password as plain text (since we don't have authentication)
        user.setPassword(request.password());

        userRepository.save(user);
        UserResponse base = userMapperImpl.entityToDto(user);
        return new UserResponse(base.id(), base.username(), base.email(), base.role(), null);
    }
}
