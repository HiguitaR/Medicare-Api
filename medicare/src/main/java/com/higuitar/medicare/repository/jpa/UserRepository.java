package com.higuitar.medicare.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.higuitar.medicare.model.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}