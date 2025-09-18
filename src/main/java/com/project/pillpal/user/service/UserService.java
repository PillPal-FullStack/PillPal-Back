package com.project.pillpal.user.service;

import com.project.pillpal.user.dtos.UserMapperImpl;
import com.project.pillpal.user.dtos.UserRequest;
import com.project.pillpal.user.dtos.UserResponse;
import com.project.pillpal.security.jwt.JwtService;
import com.project.pillpal.exceptions.EntityNotFoundException;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.user.repository.UserRepository;
import com.project.pillpal.security.UserDetail;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public UserDetail loadUserByUsername(String username) throws EntityNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new EntityNotFoundException(User.class.getSimpleName(), username));
        return new UserDetail(user);
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

        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        String token = jwtService.generateToken(new UserDetail(user));
        UserResponse base = userMapperImpl.entityToDto(user);
        return new UserResponse(base.id(), base.username(), base.email(), base.role(), token);
    }
}
