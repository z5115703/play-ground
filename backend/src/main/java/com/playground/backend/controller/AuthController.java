package com.playground.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playground.backend.dto.ChangePasswordRequest;
import com.playground.backend.dto.ChangePasswordResult;
import com.playground.backend.dto.LoginRequest;
import com.playground.backend.dto.LoginResult;
import com.playground.backend.dto.SignupRequest;
import com.playground.backend.dto.UpdateUserRequest;
import com.playground.backend.dto.UpdateUserResult;
import com.playground.backend.service.AuthService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest request) {
        boolean registerResult = authService.register(request);
        if (registerResult) {
            return ResponseEntity.ok(request);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request) {
        LoginResult result = authService.login(request);
        switch (result.getStatus()) {
            case USER_NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            case INVALID_PASSWORD -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            case SUCCESS -> {
                return ResponseEntity.ok(result.getToken());
            }
        }
        throw new IllegalStateException("Unexpected login status");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUserResponse());
    }

    @PatchMapping("/me")
    public ResponseEntity<?> updateCurrentUser(@RequestBody UpdateUserRequest request) {
        UpdateUserResult result = authService.updateCurrentUser(request); 
        switch (result.getStatus()) {
            case USERNAME_ALREADY_EXISTS -> {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }
            case SUCCESS -> {
                return ResponseEntity.ok(result.getUserResponse());
            }
        }     
        throw new IllegalStateException("Unexpected user update status");
    }

    @PatchMapping("/me/password")
    public ResponseEntity<?> updatePassword(@RequestBody ChangePasswordRequest request) {
        ChangePasswordResult result = authService.changePassword(request);
        switch(result.getStatus()) {
            case CURRENT_PASSWORD_INCORRECT -> {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }
            case SUCCESS -> {
                return ResponseEntity.ok().build();
            }
        }
        throw new IllegalStateException("Unexpected change password status");
    }
}
