package com.higuitar.medicare.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name="patients")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Patient extends DateEntity{
    @Id()
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="patient_id")
    private Long idPatient;

    private LocalDate dateOfBirth;

    private String phoneNumber;

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToMany(mappedBy = "patient")
    private List<Appointment> appointments;
}
