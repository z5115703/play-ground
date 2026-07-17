package com.playground.backend.service;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.playground.backend.dto.ChangePasswordRequest;
import com.playground.backend.dto.ChangePasswordResult;
import com.playground.backend.dto.ChangePasswordStatus;
import com.playground.backend.dto.LoginRequest;
import com.playground.backend.dto.LoginResult;
import com.playground.backend.dto.LoginStatus;
import com.playground.backend.dto.SignupRequest;
import com.playground.backend.dto.UpdateUserRequest;
import com.playground.backend.dto.UpdateUserResult;
import com.playground.backend.dto.UpdateUserStatus;
import com.playground.backend.model.User;
import com.playground.backend.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock 
    private PasswordEncoder passwordEncoder;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private AuthService authService;

    private User createUser(Long id, String name, String username, String password) {
        try {
            User user = new User(name, username, password);
        
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);

            return user;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void register_DuplicateUsername_ReturnsFalse() {
        SignupRequest request = new SignupRequest("name", "username", "password");

        when(userRepository.existsByUsername(request.username()))
                .thenReturn(true);

        assertFalse(authService.register(request));

        verify(userRepository).existsByUsername(request.username());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ValidRequest_ReturnsTrue() {
        SignupRequest request = new SignupRequest("name", "username", "password");
        
        when(userRepository.existsByUsername(request.username()))
                .thenReturn(false);

        assertTrue(authService.register(request));

        verify(userRepository).existsByUsername(request.username());
        verify(passwordEncoder).encode("password");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_InvalidUsername_ReturnsUserNotFound() {
        LoginRequest request = new LoginRequest("username", "password");
        
        when(userRepository.findByUsername(request.username()))
                .thenReturn(null);

        LoginResult result = authService.login(request);

        assertEquals(LoginStatus.USER_NOT_FOUND, result.getStatus());
        assertNull(result.getToken());

        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder, never()).matches(eq(request.password()), anyString());
    }

    @Test
    void login_InvalidPassword_ReturnsInvalidPassword() {
        LoginRequest request = new LoginRequest("username", "password");
        User user = new User("name", request.username(), "encodedPassword");
        
        when(userRepository.findByUsername(request.username()))
                .thenReturn(user);

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(false);

        LoginResult result = authService.login(request);

        assertEquals(LoginStatus.INVALID_PASSWORD, result.getStatus());
        assertNull(result.getToken());

        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
    }

    @Test
    void login_ValidRequest_ReturnsSuccess() {
        LoginRequest request = new LoginRequest("username", "password");
        User user = createUser(1L, "name", request.username(), "encodedPassword");
        
        when(userRepository.findByUsername(request.username()))
                .thenReturn(user);

        when(passwordEncoder.matches(request.password(), user.getPassword()))
                .thenReturn(true);

        LoginResult result = authService.login(request);

        assertEquals(LoginStatus.SUCCESS, result.getStatus());
        assertNotNull(result.getToken());

        verify(userRepository).findByUsername(request.username());
        verify(passwordEncoder).matches(request.password(), user.getPassword());
    }

    @Test
    void updateCurrentUser_DuplicateUsername_ReturnsUsernameAlreadyExists() {
        UpdateUserRequest request = new UpdateUserRequest("newName", "newUsername");
        User user = new User("name", "username", "password");

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(userRepository.existsByUsername(request.username()))
                .thenReturn(true);
        
        UpdateUserResult result = authService.updateCurrentUser(request);

        assertEquals(UpdateUserStatus.USERNAME_ALREADY_EXISTS, result.getStatus());
        assertNull(result.getUserResponse());

        verify(userRepository).existsByUsername(request.username());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateCurrentUser_ValidRequest_ReturnsSuccess() {
        UpdateUserRequest request = new UpdateUserRequest("newName", "newUsername");
        User user = createUser(1L, "name", "username", "password");

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(userRepository.existsByUsername(request.username()))
                .thenReturn(false);

        UpdateUserResult result = authService.updateCurrentUser(request);

        assertEquals(UpdateUserStatus.SUCCESS, result.getStatus());
        assertNotNull(result.getUserResponse());
        assertEquals(request.name(), user.getName());
        assertEquals(request.username(), user.getUsername());
        assertEquals(1L, result.getUserResponse().id());
        assertEquals(request.name(), result.getUserResponse().name());
        assertEquals(request.username(), result.getUserResponse().username());

        verify(userRepository).existsByUsername(request.username());
        verify(userRepository).save(user);
    }

    @Test
    void changePassword_InvalidCurrentPassword_ReturnsCurrentPasswordIncorrect() {
        User user = new User("name", "username", "password");

        ChangePasswordRequest request = new ChangePasswordRequest(
            "wrongPassword",
            "newPassword"
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(request.currentPassword(), user.getPassword()))
                .thenReturn(false);

        ChangePasswordResult result = authService.changePassword(request);

        assertEquals(ChangePasswordStatus.CURRENT_PASSWORD_INCORRECT, result.getStatus());

        verify(userRepository, never()).save(user);
    }

    @Test
    void changePassword_ValidCurrentPassword_ReturnsSuccess() {
        User user = new User("name", "username", "password");

        ChangePasswordRequest request = new ChangePasswordRequest(
                "correctPassword",
                "newPassword"
        );

        when(currentUserService.getCurrentUser())
                .thenReturn(user);

        when(passwordEncoder.matches(request.currentPassword(), user.getPassword()))
                .thenReturn(true);

        when(passwordEncoder.encode(request.newPassword()))
                .thenReturn("encodedPassword");

        ChangePasswordResult result = authService.changePassword(request);
        
        assertEquals("encodedPassword", user.getPassword());
        assertEquals(ChangePasswordStatus.SUCCESS, result.getStatus());

        verify(userRepository).save(user);
    }

}