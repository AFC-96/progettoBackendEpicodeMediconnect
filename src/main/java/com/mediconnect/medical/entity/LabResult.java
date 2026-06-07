package com.mediconnect.medical.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/*
Entità figlia di MedicalRecord che rappresenta un risultato di laboratorio.
La tabella "lab_results" è collegata a "medical_records" tramite @PrimaryKeyJoinColumn (strategia JOINED).
Contiene i dati dell'esame: nome test, valore, unità di misura, intervallo di riferimento e flag anomalia.
*/
@Entity
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "lab_results")
@PrimaryKeyJoinColumn(name = "medical_record_id")
public class LabResult extends MedicalRecord {

    // Nome dell'esame (es. "Emocromo", "Glicemia", "Colesterolo")
    @Column(name = "test_name", nullable = false)
    private String testName;

    // Valore del risultato (es. "120", "5.6", "positivo")
    @Column(columnDefinition = "TEXT", name = "result_value")
    private String resultValue;

    // Unità di misura (es. "mg/dL", "mmol/L", "U/L")
    @Column(name = "unit")
    private String unit;

    // Intervallo di riferimento normale (es. "70-100 mg/dL")
    @Column(name = "reference_range")
    private String referenceRange;

    // Flag che indica se il risultato è anomalo (fuori dal range di riferimento)
    // Default false — viene impostato a true dal medico se il valore è fuori norma
    @Builder.Default
    @Column(name = "is_abnormal")
    private Boolean isAbnormal = false;

    // Nome del laboratorio che ha eseguito l'analisi
    @Column(name = "lab_name")
    private String labName;
}
