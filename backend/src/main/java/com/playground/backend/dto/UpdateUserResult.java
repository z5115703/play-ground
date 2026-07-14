package com.playground.backend.dto;

public class UpdateUserResult {
    private final UpdateUserStatus status;
    private final UserResponse user;

    public UpdateUserResult(UpdateUserStatus status, UserResponse user) {
        this.status = status;
        this.user = user;
    }

    public UpdateUserStatus getStatus() {
        return this.status;
    }

    public UserResponse getUserResponse() {
        return this.user;
    }
}