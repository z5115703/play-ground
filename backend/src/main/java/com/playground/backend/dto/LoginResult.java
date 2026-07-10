package com.playground.backend.dto;

public class LoginResult {

    private final LoginStatus status;
    private final String token;

    public LoginResult(LoginStatus status, String token) {
        this.status = status;
        this.token = token;
    }

    public LoginStatus getStatus() {
        return this.status;
    }

    public String getToken() {
        return this.token;
    }
}