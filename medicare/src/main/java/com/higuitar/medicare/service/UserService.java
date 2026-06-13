package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse>  findAll();
    UserResponse findById(Long userId);
    UserResponse create(CreateUserRequest request);
    UserResponse update(Long userId, UpdateUserRequest request);
    void delete(Long userId);
}
