package com.mediconnect.users.service;

import com.mediconnect.res.Response;
import com.mediconnect.users.dto.LoginRequest;
import com.mediconnect.users.dto.LoginResponse;
import com.mediconnect.users.dto.RegistrationRequest;
import com.mediconnect.users.dto.ResetPasswordRequest;

/*
 Interfaccia che definisce il contratto per i processi di autenticazione.
 Regola registrazione, login e reset della password (forget & reset).
 */
public interface AuthService {

    // Effettua la registrazione dell'utente e la creazione automatica del profilo anagrafico
    Response<String> register(RegistrationRequest request);

    // Valida le credenziali e restituisce il token JWT (LoginResponse)
    Response<LoginResponse> login(LoginRequest loginRequest);

    // Gestisce la richiesta di reset inviando il codice segreto via email
    Response<?> forgetPassword(String email);

    // Valida il codice segreto e aggiorna la password dell'utente
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
