package com.playground.backend.dto;

public class ChangePasswordResult {
    private final ChangePasswordStatus status;
    
    public ChangePasswordResult(ChangePasswordStatus status) {
        this.status = status;
    }

    public ChangePasswordStatus getStatus() {
        return this.status;
    }
}