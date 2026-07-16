package com.playground.backend.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
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