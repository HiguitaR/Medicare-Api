package com.higuitar.medicare.model.entity;

import com.higuitar.medicare.model.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="users")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class User extends DateEntity{
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    private String name;

    private String password;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;
}
