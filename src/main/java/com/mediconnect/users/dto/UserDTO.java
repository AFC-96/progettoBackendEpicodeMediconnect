package com.mediconnect.users.dto;

import com.mediconnect.role.entity.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/*
 DTO per il trasferimento dei dati dell'account utente.
 Utilizzato per esporre le informazioni del profilo senza rivelare dati sensibili 
 come la password (grazie all'annotazione @JsonIgnore).
 Viene spesso nidificato in altri DTO (es. PatientDTO, DoctorDTO).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {
    private Long id;

    private String name;
    private String email;
    private String profilePictureUrl;

    // Nasconde la password cifrata nelle risposte JSON
    @JsonIgnore
    private String password;

    // Lista dei ruoli assegnati all'utente
    private List<Role> roles;
}
