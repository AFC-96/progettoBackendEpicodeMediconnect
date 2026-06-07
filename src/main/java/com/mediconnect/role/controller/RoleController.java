package com.mediconnect.role.controller;

import com.mediconnect.res.Response;
import com.mediconnect.role.entity.Role;
import com.mediconnect.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/*
 Controller REST che espone gli endpoint sotto /api/roles.
 Permette la gestione completa del dizionario dei ruoli di sistema.
 Tutti gli endpoint sono protetti e accessibili ESCLUSIVAMENTE agli utenti con ruolo ADMIN,
 poiché la modifica dei ruoli impatta direttamente sulla sicurezza dell'applicazione.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')") // Applica la restrizione a TUTTI gli endpoint della classe
public class RoleController {

    // Servizio per la gestione dei ruoli
    private final RoleService roleService;

    // POST /api/roles — Crea un nuovo ruolo
    @PostMapping
    public ResponseEntity<Response<Role>> createRole(@RequestBody Role roleRequest) {
        return ResponseEntity.ok(roleService.createRole(roleRequest));
    }

    // PUT /api/roles — Aggiorna il nome di un ruolo esistente
    @PutMapping
    public ResponseEntity<Response<Role>> updateRole(@RequestBody Role roleRequest) {
        return ResponseEntity.ok(roleService.updateRole(roleRequest));
    }

    // GET /api/roles — Restituisce la lista di tutti i ruoli configurati nel
    // sistema
    @GetMapping
    public ResponseEntity<Response<List<Role>>> getAllRoles() {
        return ResponseEntity.ok(roleService.getAllRoles());
    }

    // DELETE /api/roles/{id} — Elimina un ruolo dal sistema (pericoloso se ci sono
    // utenti collegati)
    @DeleteMapping("/{id}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable Long id) {
        return ResponseEntity.ok(roleService.deleteRole(id));
    }
}
