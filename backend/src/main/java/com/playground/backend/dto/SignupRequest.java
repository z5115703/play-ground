package com.playground.backend.dto;

public record SignupRequest(
    String name,
    String username,
    String password
) {
  
}