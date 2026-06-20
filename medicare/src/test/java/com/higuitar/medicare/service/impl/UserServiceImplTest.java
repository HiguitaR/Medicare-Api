package com.higuitar.medicare.service.impl;

//Mockito
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import static org.mockito.Mockito.*;

//JUnit5
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

//Objects
import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import com.higuitar.medicare.model.entity.User;
import com.higuitar.medicare.repository.jpa.UserRepository;
import com.higuitar.medicare.util.mapper.UserMapper;
import com.higuitar.medicare.exception.*;
import static com.higuitar.medicare.model.Role.PATIENT;

//Java
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void create_User_Success(){
        //give:
        CreateUserRequest request = new CreateUserRequest(
                "Juan",
                "juquita123",
                "juan@email.com",
                PATIENT
        );

        User user = new User(null,"Juan", "juquita123", "juan@email.com", PATIENT);

        User savedEntity = new User(1L, "Juan", "juquita123", "juan@email.com", PATIENT);

        UserResponse expected = new UserResponse(1L, "Juan", "juan@email.com", PATIENT);

        //when
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.empty());
        when(userMapper.toEntity(request)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedEntity);
        when(userMapper.toUserResponse(savedEntity)).thenReturn(expected);

        UserResponse result = userService.create(request);

        //then
        assertEquals(expected.userId(), result.userId());
        assertEquals(expected.email(), result.email());
        assertEquals(expected.name(), result.name());
        assertEquals(expected.role(), result.role());

        verify(userRepository).save(user);
    }

    @Test
    void create_User_EmailAlreadyExists_ThrowException(){
        //given
        CreateUserRequest request = new CreateUserRequest(
                "Juan",
                "juquita123",
                "juan@email.com",
                PATIENT
        );
        User existingUser = new User(1l, "Camilo", "milito123", "juan@email.com", PATIENT);

        //when
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        //then
        assertThrows(UserAlreadyExistException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }
    void findById_UserFound_ReturnsUserResponse(){
        //TODO
    }

    void findById_UserNotFound_ThrowsException(){
        //TODO
    }

    void findAll_ReturnsUserList(){
        //TODO
    }

    void update_User_Success(){
        //TODO
    }

    void update_EmailAlreadyTaken_ThrowsException(){
        //TODO
    }

    void delete_User_Success(){
        //TODO
    }

    void delete_UserNotFound_ThrowsException(){
        //TODO
    }
}
