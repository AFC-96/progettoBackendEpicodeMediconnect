package com.mediconnect.patient.controller;

import com.mediconnect.patient.dto.PatientDTO;
import com.mediconnect.patient.service.PatientService;
import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/*
 Controller REST che espone gli endpoint sotto /api/patients.
 Consente ai pazienti di visualizzare e aggiornare il proprio profilo,
 e ai medici (o admin) di consultare il profilo di uno specifico paziente.
 Utilizza controlli @PreAuthorize per garantire l'accesso basato sui ruoli.
 */
@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    // GET /api/patients/me — Recupera il profilo del paziente attualmente autenticato
    // Solo l'utente con ruolo PATIENT può accedere
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<PatientDTO>> getMyProfile() {
        return ResponseEntity.ok(patientService.getMyProfile());
    }

    // PUT /api/patients/me — Aggiorna il profilo (dati anagrafici o clinici) del paziente autenticato
    // Solo l'utente con ruolo PATIENT può accedere
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<PatientDTO>> updateMyProfile(@RequestBody PatientDTO patientDTO) {
        return ResponseEntity.ok(patientService.updateMyProfile(patientDTO));
    }

    // GET /api/patients/{id} — Recupera i dati completi di un paziente tramite il suo ID
    // Utilizzato dai medici per consultare la scheda del paziente prima di una visita
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<PatientDTO>> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(patientService.getPatientById(id));
    }
}
