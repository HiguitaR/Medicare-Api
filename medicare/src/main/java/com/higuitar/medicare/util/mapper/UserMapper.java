package com.higuitar.medicare.util.mapper;

import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toUserResponse(User user);
    User toEntity(CreateUserRequest request);
    void updateEntity(UpdateUserRequest request, @MappingTarget User user);
}
