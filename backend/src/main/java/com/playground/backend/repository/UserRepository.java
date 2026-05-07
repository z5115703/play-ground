package com.playground.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playground.backend.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    User findByUsername(String username);
}
