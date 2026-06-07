package com.mediconnect.medical.controller;

import com.mediconnect.medical.dto.LabResultDTO;
import com.mediconnect.medical.dto.MedicalRecordDTO;
import com.mediconnect.medical.dto.PrescriptionDTO;
import com.mediconnect.medical.service.MedicalRecordService;
import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
// Controller REST cartella clinica: prescrizioni e esami (DOCTOR), storico sanitario (PATIENT)

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
public class MedicalRecordController {

    // Servizio per la cartella clinica
    private final MedicalRecordService medicalRecordService;

    // --- Prescrizioni ---

    // Endpoint POST /prescriptions - Crea un nuovo record. Solo ruolo DOCTOR
    @PostMapping("/prescriptions")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<PrescriptionDTO>> createPrescription(@RequestBody PrescriptionDTO dto) {
        return ResponseEntity.ok(medicalRecordService.createPrescription(dto));
    }

    // Endpoint GET /prescriptions/appointment/{appointmentId} - Recupera i dati
    // richiesti
    @GetMapping("/prescriptions/appointment/{appointmentId}")
    public ResponseEntity<Response<List<PrescriptionDTO>>> getPrescriptionsByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(medicalRecordService.getPrescriptionsByAppointment(appointmentId));
    }

    // Endpoint GET /prescriptions/me - Recupera i dati richiesti. Solo ruolo
    // PATIENT
    @GetMapping("/prescriptions/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<PrescriptionDTO>>> getMyPrescriptions() {
        return ResponseEntity.ok(medicalRecordService.getMyPrescriptions());
    }

    // --- Risultati di Laboratorio ---

    // Endpoint POST /lab-results - Crea un nuovo record. Solo ruolo DOCTOR
    @PostMapping("/lab-results")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<LabResultDTO>> createLabResult(@RequestBody LabResultDTO dto) {
        return ResponseEntity.ok(medicalRecordService.createLabResult(dto));
    }

    // Endpoint GET /lab-results/appointment/{appointmentId} - Recupera i dati
    // richiesti
    @GetMapping("/lab-results/appointment/{appointmentId}")
    public ResponseEntity<Response<List<LabResultDTO>>> getLabResultsByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(medicalRecordService.getLabResultsByAppointment(appointmentId));
    }

    // Endpoint GET /lab-results/me - Recupera i dati richiesti. Solo ruolo PATIENT
    @GetMapping("/lab-results/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<LabResultDTO>>> getMyLabResults() {
        return ResponseEntity.ok(medicalRecordService.getMyLabResults());
    }

    // --- Storico Medico Completo ---

    // Endpoint GET /history/me - Recupera i dati richiesti. Solo ruolo PATIENT
    @GetMapping("/history/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<MedicalRecordDTO>>> getMyMedicalHistory() {
        return ResponseEntity.ok(medicalRecordService.getMyMedicalHistory());
    }
}
