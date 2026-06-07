package com.mediconnect.patient.dto;

import com.mediconnect.enums.BloodGroup;
import com.mediconnect.enums.Genotype;
import com.mediconnect.users.dto.UserDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/*
 DTO per il trasferimento dati dei profili pazienti.
 Permette al frontend di leggere e aggiornare le informazioni cliniche e anagrafiche,
 nascondendo la struttura del database e includendo in modo sicuro i dati dell'account utente.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PatientDTO {

    private Long id;

    // Dati anagrafici
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private String phone;

    // Dati clinici e anamnesi
    private String knownAllergies;
    private BloodGroup bloodGroup;
    private Genotype genotype;

    // Riferimento all'account utente (popolato dal ModelMapper)
    private UserDTO user;
}
