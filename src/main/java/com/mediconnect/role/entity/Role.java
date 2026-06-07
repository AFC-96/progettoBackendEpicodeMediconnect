package com.mediconnect.role.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// Questa entità definisce la struttura della tabella roles nel database per gestire i permessi degli utenti (PATIENT, DOCTOR, ADMIN) nell'applicazione Mediconnect. 

@Entity // Mappa la classe a una tabella DB
@Data // Genera getter, setter, toString, ecc.
@Builder // Abilita il pattern Builder per la creazione
@AllArgsConstructor // Costruttore con tutti i campi
@NoArgsConstructor // Costruttore vuoto (richiesto da JPA)
@Table(name = "roles") // Nome tabella nel DB
public class Role {

    @Id // Chiave primaria
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-incremento
    private Long id;

    // Nome del ruolo, deve essere univoco (es. "ADMIN", "DOCTOR", "PATIENT")
    // Questo valore viene usato da Spring Security nelle annotazioni @PreAuthorize
    @Column(unique = true)
    private String name;

}
