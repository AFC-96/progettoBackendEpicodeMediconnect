package com.mediconnect.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

// DTO per il reset password: email per la richiesta, codice e nuova password per la conferma
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {

    // Usato per richiedere il reset della password
    private String email;

    // Usato per impostare la nuova password
    private String code;
    private String newPassword;
}