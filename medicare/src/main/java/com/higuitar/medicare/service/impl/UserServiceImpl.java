package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import com.higuitar.medicare.service.UserService;

import java.util.List;

public class UserServiceImpl implements UserService {
    @Override
    public List<UserResponse> findAll() {
        return List.of();
    }

    @Override
    public UserResponse findById(Long userId) {
        return null;
    }

    @Override
    public UserResponse create(CreateUserRequest request) {
        return null;
    }

    @Override
    public UserResponse update(Long userId, UpdateUserRequest request) {
        return null;
    }

    @Override
    public void delete(Long userId) {

    }
}
