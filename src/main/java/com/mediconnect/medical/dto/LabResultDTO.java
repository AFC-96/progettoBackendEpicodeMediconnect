package com.mediconnect.medical.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO per il trasferimento dati degli esami di laboratorio
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LabResultDTO {
    private Long id;
    private Long appointmentId;
    private LocalDateTime createdAt;
    private String notes;
    private String testName;
    private String resultValue;
    private String unit;
    private String referenceRange;
    private Boolean isAbnormal;
    private String labName;
}
