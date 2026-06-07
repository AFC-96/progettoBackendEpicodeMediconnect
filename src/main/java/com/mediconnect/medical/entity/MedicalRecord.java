package com.mediconnect.medical.entity;

import com.mediconnect.appointment.entity.Appointment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/*
Classe base ASTRATTA della cartella clinica. Non può essere istanziata direttamente.
Usa la strategia di ereditarietà JOINED: la tabella "medical_records" contiene i campi comuni,
mentre le sotto-classi Prescription e LabResult hanno le proprie tabelle collegate tramite FK.
@SuperBuilder (Lombok) permette l'ereditarietà del pattern Builder nelle sotto-classi.
 */
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "medical_records")
public abstract class MedicalRecord {

    // Chiave primaria condivisa con le sotto-classi tramite @PrimaryKeyJoinColumn
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relazione ManyToOne: ogni record clinico è collegato a UN appuntamento
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    // Data di creazione auto-generata da Hibernate al momento del salvataggio
    // updatable = false impedisce che venga modificata dopo la creazione
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Note generiche del medico (campo comune a prescrizioni e esami)
    @Column(columnDefinition = "TEXT")
    private String notes;
}
