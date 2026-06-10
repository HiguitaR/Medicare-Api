package com.higuitar.medicare.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name="doctors")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Doctor extends DateEntity {
    @Id
    @Column(name="doctor_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDoctor;

    private String specialization;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "doctor")
    private List<Appointment> appointments;
}
