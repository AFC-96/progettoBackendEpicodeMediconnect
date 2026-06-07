package com.mediconnect.medical.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

/*
 Entità figlia di MedicalRecord che rappresenta una prescrizione farmaceutica.
 La tabella "prescriptions" è collegata a "medical_records" tramite @PrimaryKeyJoinColumn (strategia JOINED).
 @EqualsAndHashCode(callSuper = true) include i campi della classe padre nel confronto.
 */
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "prescriptions")
@PrimaryKeyJoinColumn(name = "medical_record_id")
public class Prescription extends MedicalRecord {

    // Nome del farmaco prescritto (obbligatorio)
    @Column(name = "drug_name", nullable = false)
    private String drugName;

    // Dosaggio (es. "500mg", "10ml")
    @Column(name = "dosage")
    private String dosage;

    // Frequenza di assunzione (es. "2 volte al giorno", "ogni 8 ore")
    @Column(name = "frequency")
    private String frequency;

    // Durata del trattamento in giorni
    @Column(name = "duration_days")
    private Integer durationDays;

    // Data di scadenza della prescrizione
    @Column(name = "valid_until")
    private LocalDate validUntil;

    // Istruzioni aggiuntive per il paziente (es. "assumere dopo i pasti")
    @Column(columnDefinition = "TEXT", name = "instructions")
    private String instructions;
}
