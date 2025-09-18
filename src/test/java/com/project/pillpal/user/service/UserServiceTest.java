package com.project.pillpal.user.service;

import com.project.pillpal.exceptions.EntityNotFoundException;
import com.project.pillpal.user.dtos.UserMapperImpl;
import com.project.pillpal.user.dtos.UserRequest;
import com.project.pillpal.user.dtos.UserResponse;
import com.project.pillpal.user.entity.Role;
import com.project.pillpal.user.entity.User;
import com.project.pillpal.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapperImpl userMapperImpl;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserRequest userRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setRole(Role.ROLE_USER);

        userRequest = new UserRequest("testuser", "test@example.com", "password123");
        userResponse = new UserResponse(1L, "testuser", "test@example.com", Role.ROLE_USER, null);
    }

    @Test
    void testFindByUsernameSuccess() {
        when(userRepository.findByUsernameIgnoreCase("testuser")).thenReturn(Optional.of(user));

        User result = userService.findByUsername("testuser");

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository).findByUsernameIgnoreCase("testuser");
    }

    @Test
    void testFindByUsernameNotFound() {
        when(userRepository.findByUsernameIgnoreCase("nonexistent")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findByUsername("nonexistent"));
        verify(userRepository).findByUsernameIgnoreCase("nonexistent");
    }

    @Test
    void testFindByIdSuccess() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindByIdNotFound() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.findById(999L));
        verify(userRepository).findById(999L);
    }

    @Test
    void testRegisterUserSuccess() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapperImpl.dtoToEntity(any(UserRequest.class), any())).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapperImpl.entityToDto(user)).thenReturn(userResponse);

        UserRequest request = new UserRequest("newuser", "new@example.com", "password123");
        UserResponse result = userService.registerUser(request);

        assertNotNull(result);
        assertEquals("testuser", result.username());
        assertEquals("test@example.com", result.email());
        assertEquals(Role.ROLE_USER, result.role());
        assertNull(result.token());

        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("new@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingUsername() {
        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        UserRequest request = new UserRequest("existinguser", "new@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        verify(userRepository).existsByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUserWithExistingEmail() {
        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        UserRequest request = new UserRequest("newuser", "existing@example.com", "password123");

        assertThrows(IllegalArgumentException.class, () -> userService.registerUser(request));
        verify(userRepository).existsByUsername("newuser");
        verify(userRepository).existsByEmail("existing@example.com");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUserWithNullRole() {
        User userWithoutRole = new User();
        userWithoutRole.setId(1L);
        userWithoutRole.setUsername("newuser");
        userWithoutRole.setEmail("new@example.com");
        userWithoutRole.setPassword("password123");
        userWithoutRole.setRole(null);

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userMapperImpl.dtoToEntity(any(UserRequest.class), any())).thenReturn(userWithoutRole);
        when(userRepository.save(any(User.class))).thenReturn(userWithoutRole);
        when(userMapperImpl.entityToDto(userWithoutRole)).thenReturn(userResponse);

        UserRequest request = new UserRequest("newuser", "new@example.com", "password123");
        UserResponse result = userService.registerUser(request);

        assertNotNull(result);
        verify(userRepository).save(any(User.class));
        verify(userMapperImpl).entityToDto(userWithoutRole);
    }

}
