package com.playground.backend.dto;

public record UserResponse(
    Long id,
    String username,
    String name
) {}