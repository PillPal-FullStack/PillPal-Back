package com.project.pillpal.user.service;

import com.project.pillpal.user.dtos.UserMapperImpl;
import com.project.pillpal.user.dtos.UserRequest;
import com.project.pillpal.user.dtos.UserResponse;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.user.repository.UserRepository;
import com.project.pillpal.security.UserDetail;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserMapperImpl userMapperImpl;
    private final BCryptPasswordEncoder passwordEncoder;

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

        Role requestedRole = request.role();

        User user = userMapperImpl.dtoToEntity(request);

        user.setPassword(passwordEncoder.encode(request.password()));

        userRepository.save(user);
        return userMapperImpl.entityToDto(user);
    }
}
