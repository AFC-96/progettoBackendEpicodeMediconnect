package com.mediconnect.consultation.service;

import com.mediconnect.consultation.dto.ConsultationDTO;
import com.mediconnect.res.Response;

import java.util.List;
/*
 * Interfaccia che definisce il contratto del servizio consulenze.
 * Dichiara le operazioni disponibili senza esporre i dettagli implementativi,
 * permettendo al Controller di dipendere dall'interfaccia e non dall'implementazione.
 */
public interface ConsultationService {
    // Crea un nuovo referto SOAP collegato a un appuntamento
    Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);
    // Recupera il referto di un appuntamento specifico tramite il suo ID
    Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);
    // Recupera tutti i referti del paziente loggato
    Response<List<ConsultationDTO>> getMyConsultations();
    // Aggiorna i campi SOAP di un referto esistente
    Response<ConsultationDTO> updateConsultation(Long id, ConsultationDTO consultationDTO);
}
