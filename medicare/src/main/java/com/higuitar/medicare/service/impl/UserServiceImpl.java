package com.higuitar.medicare.service.impl;

import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import com.higuitar.medicare.exception.UserAlreadyExistException;
import com.higuitar.medicare.exception.UserNotFoundException;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.service.UserService;
import com.higuitar.medicare.util.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> findAll() {

        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    @Override
    public UserResponse findById(Long userId) {

        return userRepository.findById(userId)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new UserNotFoundException("User with id: " + userId + " Not found!"));
    }

    @Override
    public UserResponse create(CreateUserRequest request) {

        if(userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistException("User already exist with email: " + request.email());
        }
        User newUser = userMapper.toEntity(request);
        newUser = userRepository.save(newUser);
        return userMapper.toUserResponse(newUser);
    }

    @Override
    public UserResponse update(Long userId, UpdateUserRequest request) {

        userRepository.findByEmail(request.email())
                .ifPresent(existingUser -> {
                    if (!existingUser.getUserId().equals(userId)) {
                        throw new UserAlreadyExistException("User already exist: " + request.email());
                    }
                });

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userMapper.updateEntity(request, user);
        return userMapper.toUserResponse(userRepository.save(user));
    }

    @Override
    public void delete(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
        log.info("User : {} has been deleted!", user.getName());
    }
}
