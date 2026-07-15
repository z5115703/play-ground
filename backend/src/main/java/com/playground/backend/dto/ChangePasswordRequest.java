package com.playground.backend.dto;

public record ChangePasswordRequest(
    String currentPassword,
    String newPassword
) {}