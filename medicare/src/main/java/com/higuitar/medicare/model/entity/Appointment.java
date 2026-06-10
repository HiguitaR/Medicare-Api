package com.higuitar.medicare.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.higuitar.medicare.model.AppointmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Entity
@Table(name="appointments")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Appointment extends DateEntity{
    @Id
    @Column(name="appointment_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long idAppointment;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status;

    private LocalDateTime dateTime;

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    private Patient patient;

    @ManyToOne
    @JsonIgnoreProperties("appointments")
    private Doctor doctor;
}
