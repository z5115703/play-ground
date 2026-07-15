package com.playground.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.playground.backend.dto.UserResponse;
import com.playground.backend.model.User;
import com.playground.backend.repository.UserRepository;
import com.playground.backend.util.JwtUtil;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean register(SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return false;
        }

        User user = new User(request.name(), request.username(), passwordEncoder.encode(request.password()));
        userRepository.save(user);

        return true;
    }

    public LoginResult login(LoginRequest request) {
        User existingUser = userRepository.findByUsername(request.username());

        if(existingUser == null) {
            return new LoginResult(LoginStatus.USER_NOT_FOUND, null);
        }

        if (!passwordEncoder.matches(request.password(), existingUser.getPassword())) {
            return new LoginResult(LoginStatus.INVALID_PASSWORD, null);
        }

        //String token = JwtUtil.generateToken(existingUser.getUsername());
        String token = JwtUtil.generateToken(String.valueOf(existingUser.getId()));
        return new LoginResult(LoginStatus.SUCCESS, token);
    }

    public User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long userId = Long.valueOf(auth.getName());

        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public UserResponse getCurrentUserResponse() {
        User user = getCurrentUser();
        return new UserResponse(user.getId(), user.getUsername(), user.getName());
    }

    public UpdateUserResult updateCurrentUser(UpdateUserRequest request) {
        User user = getCurrentUser();

        if(request.username() != null &&
            !request.username().equals(user.getUsername()) && 
            userRepository.existsByUsername(request.username())) {
            return new UpdateUserResult(UpdateUserStatus.USERNAME_ALREADY_EXISTS, null);
        }

        if (request.name() != null) {
            user.setName(request.name());
        }
        
        if (request.username() != null) {
           user.setUsername(request.username()); 
        }
        
        userRepository.save(user);

        return new UpdateUserResult(UpdateUserStatus.SUCCESS, new UserResponse(user.getId(), user.getUsername(), user.getName()));
    }

    public ChangePasswordResult changePassword(ChangePasswordRequest request) {
        User user = getCurrentUser();
        if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
            return new ChangePasswordResult(ChangePasswordStatus.CURRENT_PASSWORD_INCORRECT);
        }
        user.setPassword(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);

        return new ChangePasswordResult(ChangePasswordStatus.SUCCESS);
    }
}