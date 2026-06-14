package com.higuitar.medicare.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import com.higuitar.medicare.model.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}