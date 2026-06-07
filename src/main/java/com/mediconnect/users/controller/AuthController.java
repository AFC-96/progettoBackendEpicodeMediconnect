package com.mediconnect.users.controller;

import com.mediconnect.res.Response;
import com.mediconnect.users.dto.*;
import com.mediconnect.users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/*
 Controller REST pubblico per la gestione dell'autenticazione.
 Espone endpoint sotto /api/auth che non richiedono un token JWT.
 Si occupa di registrazione nuovi utenti, login (rilascio token) e
 dell'intero flusso di recupero/reset password via email.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /api/auth/register — Registra un nuovo utente e crea il profilo (Paziente o Medico)
    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@Valid @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    // POST /api/auth/login — Verifica credenziali e genera il token JWT per l'accesso
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // POST /api/auth/forget-password — Richiede l'invio dell'email con il codice/link per il reset
    @PostMapping("/forget-password")
    public ResponseEntity<Response<?>> forgetPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgetPassword(email));
    }

    // POST /api/auth/reset-password — Imposta una nuova password fornendo il codice di recupero valido
    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest request) {
        return ResponseEntity.ok(authService.updatePasswordViaResetCode(request));
    }
}
