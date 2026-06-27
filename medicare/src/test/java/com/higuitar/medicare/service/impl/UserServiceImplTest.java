package com.higuitar.medicare.service.impl;

//Mockito
import com.higuitar.medicare.dto.request.UpdateUserRequest;
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
import java.util.List;
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
        assertNotNull(result);
        assertEquals(expected.userId(), result.userId());

        verify(userRepository).save(user);
        verify(userRepository).findByEmail(request.email());
        verify(userMapper).toEntity(request);
        verify(userMapper).toUserResponse(savedEntity);

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
        User existingUser = new User(1L, "Camilo", "milito123", "juan@email.com", PATIENT);

        //when
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));

        //then
        assertThrows(UserAlreadyExistException.class, () -> userService.create(request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void findById_UserFound_ReturnsUserResponse(){
        //given
        Long userId = 1L;
        User existingUser = new User(1L, "Juan", "juquita123", "juan@email.com", PATIENT);
        UserResponse expected = new UserResponse(userId, "Juan", "juan@email.com", PATIENT);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUserResponse(existingUser)).thenReturn(expected);

        UserResponse result = userService.findById(userId);

        //then
        assertNotNull(result);
        assertEquals(expected.userId(), result.userId());
        assertEquals(expected.email(), result.email());

        verify(userRepository, times(1)).findById(userId);

    }

    @Test
    void findById_UserNotFound_ThrowsException(){
        //given
        Long userId = 1L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
        verify(userRepository).findById(userId);
    }

    @Test
    void findAll_ReturnsUserList(){
        //given
        User user = new User(1L, "juan", "juquita123",
                "juan@email.com",  PATIENT);
        UserResponse response = new UserResponse(1L, "juan",
                "juan@email.com", PATIENT);

        //when
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(response);

        List<UserResponse> result = userService.findAll();

        //then
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void update_User_Success(){
        //give
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest("Juan Modificado",
                "juquita", "juan06@email.com", PATIENT);
        User existingUser = new User(userId, "Juan", "juquita",
                "juan@email.com", PATIENT);
        UserResponse expected = new UserResponse(userId,"Juan Modificado",
                "juan06@email.com", PATIENT);

        //when
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(existingUser));
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(existingUser)).thenReturn(existingUser);
        when(userMapper.toUserResponse(existingUser)).thenReturn(expected);
        UserResponse result = userService.update(userId, request);

        //then
        assertNotNull(result);
        assertEquals(expected.name(), result.name());
        verify(userRepository).save(any(User.class));

    }

    @Test
    void update_EmailAlreadyTaken_ThrowsException(){
        //given
        Long userId = 1L;
        UpdateUserRequest request = new UpdateUserRequest("Juan Modificado",
                "juquita", "otro@email.com", PATIENT);

        User otherUser = new User(2L, "Otro", "pass",
                "otro@email.com", PATIENT);

        //when
        when(userRepository.findByEmail(request.email())).thenReturn(Optional.of(otherUser));

        //then
        assertThrows(UserAlreadyExistException.class, () -> userService.update(userId, request));
        verify(userRepository, never()).save(any());
    }

    @Test
    void delete_User_Success(){
        //given
        Long userId = 1L;
        User existingUser = new User(userId, "Juan", "pass",
                "juan@email.com", PATIENT);

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        userService.delete(userId);

        //then
        verify(userRepository).findById(userId);
        verify(userRepository).delete(existingUser);

    }

    @Test
    void delete_UserNotFound_ThrowsException(){
        //given
        Long userId = 99L;

        //when
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //then
        assertThrows(UserNotFoundException.class, () -> userService.delete(userId));
        verify(userRepository, never()).delete(any());
    }
}
