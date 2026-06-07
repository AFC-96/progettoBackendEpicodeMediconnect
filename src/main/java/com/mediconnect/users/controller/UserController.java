package com.mediconnect.users.controller;

import com.mediconnect.res.Response;
import com.mediconnect.users.dto.UpdatePasswordRequest;
import com.mediconnect.users.dto.UserDTO;
import com.mediconnect.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 Controller REST per la gestione del profilo utente.
 Espone gli endpoint sotto /api/users per permettere all'utente loggato di 
 recuperare le proprie informazioni, cambiare la password e caricare l'immagine del profilo.
 Include anche endpoint protetti per gli ADMIN per gestire l'elenco utenti.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // GET /api/users/me — Recupera le informazioni dell'account loggato
    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getMyDetails() {
        return ResponseEntity.ok(userService.getMyUserDetails());
    }

    // GET /api/users/{id} — Recupera un utente specifico tramite ID (solo per ADMIN)
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<UserDTO>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // GET /api/users — Recupera la lista di tutti gli utenti registrati (solo per ADMIN)
    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    // PUT /api/users/password — Permette all'utente di cambiare la propria password (richiede old e new password)
    @PutMapping("/password")
    public ResponseEntity<Response<?>> updatePassword(@Valid @RequestBody UpdatePasswordRequest request) {
        return ResponseEntity.ok(userService.updatePassword(request));
    }

    // POST /api/users/profile-picture — Carica una nuova foto profilo per l'utente loggato (multipart/form-data)
    @PostMapping("/profile-picture")
    public ResponseEntity<Response<?>> uploadProfilePicture(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(userService.uploadProfilePicture(file));
    }
}
