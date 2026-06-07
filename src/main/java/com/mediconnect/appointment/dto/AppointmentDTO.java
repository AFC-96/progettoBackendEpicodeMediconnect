package com.mediconnect.appointment.dto;

import com.mediconnect.doctor.dto.DoctorDTO;
import com.mediconnect.enums.AppointmentStatus;
import com.mediconnect.patient.dto.PatientDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// DTO per il trasferimento dati degli appuntamenti tra controller e servizio
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Doctor ID is required for booking.")
    private Long doctorId;

    private String purposeOfConsultation;

    private String initialSymptoms;

    @NotNull(message = "Start time is required for the appointment.")
    @Future(message = "Appointment must be scheduled for a future date and time.")
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private String meetingLink;

    private AppointmentStatus status;

    private DoctorDTO doctor;
    private PatientDTO patient;

}
