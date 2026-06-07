package com.mediconnect.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO base per la timeline clinica: campo tipo distingue PRESCRIPTION e LAB_RESULT
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MedicalRecordDTO {
    private Long id;
    private Long appointmentId;
    private LocalDateTime createdAt;
    private String notes;
    private String type; // "PRESCRIPTION" or "LAB_RESULT"
}
