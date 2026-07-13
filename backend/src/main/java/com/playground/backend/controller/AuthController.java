package com.playground.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playground.backend.dto.LoginResult;
import com.playground.backend.model.User;
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
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        boolean registerResult = authService.register(user);
        if (registerResult) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        LoginResult result = authService.login(user);
        switch (result.getStatus()) {
            case USER_NOT_FOUND -> {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            case INVALID_PASSWORD -> {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            case SUCCESS -> {
                return ResponseEntity.ok(result);
            }
        }
        throw new IllegalStateException("Unexpected login status");
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        User user = authService.getCurrentUser();  

        return ResponseEntity.ok(user);
    }
}
