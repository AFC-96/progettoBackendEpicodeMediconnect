package com.mediconnect.users.dto;

import com.mediconnect.enums.Specialization;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

// DTO di registrazione: dati anagrafici, ruolo, specializzazione e licenza (se medico)
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegistrationRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private Specialization specialization; // Se l'utente è un medico, specifica la sua specializzazione

    private String licenseNumber; // Se l'utente è un medico, numero di licenza del dottore

    @NotBlank(message = "Email is required")
    @Email // (Annotazione implicita per la validazione email, spesso aggiunta in questo
           // contesto)
    private String email;

    private List<String> roles;

    @NotBlank(message = "Password is required")
    private String password;
}