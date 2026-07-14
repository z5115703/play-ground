package com.playground.backend.dto;

public record UpdateUserRequest (
  String name,
  String username
) {

}