package com.mediconnect.consultation.dto;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/*
 * DTO per il trasferimento dati dei referti clinici SOAP tra frontend e backend.
 * Oggetto leggero che evita di esporre l'entità JPA direttamente.
 * @JsonInclude(NON_NULL) esclude i campi nulli dal JSON, @JsonIgnoreProperties ignora campi sconosciuti in input.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConsultationDTO {

    // ID del referto (presente solo nelle risposte, non nelle richieste di creazione)
    private Long id;

    // ID dell'appuntamento a cui collegare il referto (obbligatorio nella creazione)
    private Long appointmentId;

    // Data della consulenza (impostata automaticamente dal Service)
    private LocalDateTime consultationDate;

    // S — Note soggettive del paziente
    private String subjectiveNotes;

    // O — Reperti oggettivi del medico
    private String objectiveFindings;

    // A — Valutazione/diagnosi
    private String assessment;

    // P — Piano terapeutico
    private String plan;
}
