package com.higuitar.medicare.controller;



import com.higuitar.medicare.dto.request.CreateUserRequest;
import com.higuitar.medicare.dto.request.UpdateUserRequest;
import com.higuitar.medicare.dto.response.UserResponse;
import com.higuitar.medicare.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/v1/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse newUser = userService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserResponse>> findAll(){
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long id,
                                                   @Valid @RequestBody UpdateUserRequest request) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> deleteUser(@PathVariable Long id){
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
