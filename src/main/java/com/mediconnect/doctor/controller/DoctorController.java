package com.mediconnect.doctor.controller;

import com.mediconnect.doctor.dto.DoctorDTO;
import com.mediconnect.doctor.service.DoctorService;
import com.mediconnect.enums.Specialization;
import com.mediconnect.res.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Controller REST profili medici: ricerca pubblica e aggiornamento protetto
@RestController
@RequestMapping("/api/doctors")
@RequiredArgsConstructor
public class DoctorController {

    // Servizio per i profili medici
    private final DoctorService doctorService;

    // Endpoint GET - Recupera i dati richiesti
    @GetMapping
    public ResponseEntity<Response<List<DoctorDTO>>> getAllDoctors() {
        return ResponseEntity.ok(doctorService.getAllDoctors());
    }

    // Endpoint GET /{id} - Recupera i dati richiesti
    @GetMapping("/{id}")
    public ResponseEntity<Response<DoctorDTO>> getDoctorById(@PathVariable Long id) {
        return ResponseEntity.ok(doctorService.getDoctorById(id));
    }

    // Endpoint GET /specialization/{specialization} - Recupera i dati richiesti
    @GetMapping("/specialization/{specialization}")
    public ResponseEntity<Response<List<DoctorDTO>>> getBySpecialization(@PathVariable Specialization specialization) {
        return ResponseEntity.ok(doctorService.getDoctorsBySpecialization(specialization));
    }

    // Endpoint GET /me - Recupera i dati richiesti. Solo ruolo DOCTOR
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> getMyProfile() {
        return ResponseEntity.ok(doctorService.getMyProfile());
    }

    // Endpoint PUT /me - Aggiorna i dati esistenti. Solo ruolo DOCTOR
    @PutMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> updateMyProfile(@RequestBody DoctorDTO doctorDTO) {
        return ResponseEntity.ok(doctorService.updateMyProfile(doctorDTO));
    }
}
