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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * JPA-backed implementation of {@link UserService} that also acts as the
 * {@link UserDetailsService} used by Spring Security, exposing accounts with
 * {@code ROLE_}-prefixed authorities.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder pswEncoder;

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new UsernameNotFoundException("User not found with email: " + email));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }

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
        newUser.setPassword(pswEncoder.encode(request.password()));
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
        user.setPassword(pswEncoder.encode(request.password()));
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
