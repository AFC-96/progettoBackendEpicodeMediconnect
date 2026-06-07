package com.mediconnect.consultation.controller;

import com.mediconnect.consultation.dto.ConsultationDTO;
import com.mediconnect.consultation.service.ConsultationService;
import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller REST referti clinici SOAP: creazione e recupero per appuntamento

@RestController
@RequestMapping("/api/consultations")
@RequiredArgsConstructor
public class ConsultationController {

    // Servizio per i referti clinici
    private final ConsultationService consultationService;

    // Endpoint POST - Crea un nuovo record. Solo ruolo DOCTOR
    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<ConsultationDTO>> createConsultation(@RequestBody ConsultationDTO dto) {
        return ResponseEntity.ok(consultationService.createConsultation(dto));
    }

    // Endpoint GET /appointment/{appointmentId} - Recupera i dati richiesti
    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Response<ConsultationDTO>> getByAppointmentId(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(consultationService.getConsultationByAppointmentId(appointmentId));
    }

    // Endpoint GET /me - Recupera i dati richiesti. Solo ruolo PATIENT
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<ConsultationDTO>>> getMyConsultations() {
        return ResponseEntity.ok(consultationService.getMyConsultations());
    }

    // Endpoint PUT /{id} - Aggiorna i dati esistenti. Solo ruolo DOCTOR
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<ConsultationDTO>> updateConsultation(
            @PathVariable Long id,
            @RequestBody ConsultationDTO dto) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, dto));
    }
}
