package com.higuitar.medicare.service;

import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import com.higuitar.medicare.exception.UserAlreadyExistException;
import com.higuitar.medicare.exception.UserNotFoundException;

import java.util.List;

/**
 * Manages the user accounts of the system (identity, credentials and role).
 */
public interface UserService {

    /**
     * Lists all user accounts.
     *
     * @return every user in the system
     */
    List<UserResponse>  findAll();

    /**
     * Finds a user by id.
     *
     * @param userId the user id
     * @return the user account
     * @throws UserNotFoundException if no user exists with the given id
     */
    UserResponse findById(Long userId);

    /**
     * Creates a user account with any role. Restricted to ADMIN users;
     * public self-registration is handled by {@link AuthService}.
     *
     * @param request the user data including the role to assign
     * @return the created user
     * @throws UserAlreadyExistException if the email is already registered
     */
    UserResponse create(CreateUserRequest request);

    /**
     * Updates an existing user account.
     *
     * @param userId  the id of the user to update
     * @param request the new account data
     * @return the updated user
     * @throws UserNotFoundException     if no user exists with the given id
     * @throws UserAlreadyExistException if the new email is already taken by another user
     */
    UserResponse update(Long userId, UpdateUserRequest request);

    /**
     * Deletes a user account.
     *
     * @param userId the id of the user to delete
     * @throws UserNotFoundException if no user exists with the given id
     */
    void delete(Long userId);
}
