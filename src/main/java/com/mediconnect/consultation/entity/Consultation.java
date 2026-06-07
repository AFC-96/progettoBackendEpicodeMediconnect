package com.mediconnect.consultation.entity;

import com.mediconnect.appointment.entity.Appointment;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
// Entità referto clinico SOAP (Subjective, Objective, Assessment, Plan) collegato One-to-One all'appuntamento

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "consultations")

public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime consultationDate;

    @Column(columnDefinition = "TEXT")
    private String subjectiveNotes;

    @Column(columnDefinition = "TEXT")
    private String objectiveFindings;

    @Column(columnDefinition = "TEXT")
    private String assessment;

    @Column(columnDefinition = "TEXT")
    private String plan;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
}