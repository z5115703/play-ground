package com.playground.backend.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.playground.backend.dto.LoginRequest;
import com.playground.backend.dto.LoginResult;
import com.playground.backend.dto.LoginStatus;
import com.playground.backend.dto.SignupRequest;
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
}