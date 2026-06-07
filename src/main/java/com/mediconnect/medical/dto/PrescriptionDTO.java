package com.mediconnect.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

// DTO per il trasferimento dati delle prescrizioni
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrescriptionDTO {
    private Long id;
    private Long appointmentId;
    private LocalDateTime createdAt;
    private String notes;
    private String drugName;
    private String dosage;
    private String frequency;
    private Integer durationDays;
    private LocalDate validUntil;
    private String instructions;
}
