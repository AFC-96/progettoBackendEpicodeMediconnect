package com.mediconnect.appointment.controller;

import com.mediconnect.appointment.dto.AppointmentDTO;
import com.mediconnect.appointment.service.AppointmentService;
import com.mediconnect.enums.AppointmentStatus;
import com.mediconnect.res.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

//Il Controller riceve la richiesta HTTP la delega al Service, e restituisce il risultato avvolto in ResponseEntity
//Il Controller conosce solo l'interfaccia AppointmentService, non l'implementazione

@RestController // @RestController indica che questa classe gestisce richieste HTTP REST e le
                // risposte vengono automaticamente serializzate in JSON
@RequestMapping("/api/appointments") // @RequestMapping("/api/appointments") definisce il percorso base: tutte le
                                     // rotte di questo controller iniziano con /api/appointments
@RequiredArgsConstructor // @RequiredArgsConstructor (Lombok) inietta automaticamente AppointmentService
                         // nel costruttore (è l'equivalente di @Autowired)
public class AppointmentController {

    private final AppointmentService appointmentService;

    // POST /api/appointments — Prenotazione di un nuovo appuntamento
    // @PreAuthorize("hasAuthority('PATIENT')") = solo gli utenti con ruolo PATIENT
    // possono accedere
    // @Valid attiva le validazioni definite nel DTO (@NotNull, @Future) prima di
    // entrare nel metodo
    // @RequestBody deserializza il JSON della richiesta in un oggetto
    // AppointmentDTO
    @PostMapping
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<AppointmentDTO>> bookAppointment(@Valid @RequestBody AppointmentDTO dto) {
        return ResponseEntity.ok(appointmentService.bookAppointment(dto));
    }

    // GET /api/appointments — Recupera tutti gli appuntamenti dell'utente loggato
    // Il Service determina automaticamente se l'utente è un medico o un paziente e
    // restituisce la lista corrispondente
    @GetMapping
    public ResponseEntity<Response<List<AppointmentDTO>>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    // GET /api/appointments/{id} — Recupera il dettaglio di un singolo appuntamento
    // @PathVariable estrae l'ID dall'URL (es. /api/appointments/5 → id = 5)
    @GetMapping("/{id}")
    public ResponseEntity<Response<AppointmentDTO>> getAppointmentById(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.getAppointmentById(id));
    }

    // PATCH /api/appointments/{id}/cancel — Annulla un appuntamento esistente
    // Usa PATCH (non DELETE) perché non rimuove il record, ma ne cambia lo stato a
    // CANCELLED
    // Il Service invia anche una notifica email al paziente
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<Response<?>> cancelAppointment(@PathVariable Long id) {
        return ResponseEntity.ok(appointmentService.cancelAppointment(id));
    }

    // PATCH /api/appointments/{id}/status?status=COMPLETED — Aggiorna lo stato
    // dell'appuntamento
    // @PreAuthorize: solo DOCTOR o ADMIN possono cambiare lo stato (es. da
    // SCHEDULED a COMPLETED)
    // @RequestParam legge il parametro "status" dalla query string dell'URL
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAuthority('DOCTOR') or hasAuthority('ADMIN')")
    public ResponseEntity<Response<AppointmentDTO>> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateAppointmentStatus(id, status));
    }
}
