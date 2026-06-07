package com.mediconnect.patient.service;

import com.mediconnect.patient.dto.PatientDTO;
import com.mediconnect.res.Response;

/*
 Interfaccia che definisce il contratto del servizio per i profili paziente.
 Nasconde le logiche di database e autorizzazione ai Controller.
 */
public interface PatientService {
    
    // Recupera il profilo del paziente loggato
    Response<PatientDTO> getMyProfile();
    
    // Aggiorna in modo parziale il profilo del paziente loggato
    Response<PatientDTO> updateMyProfile(PatientDTO patientDTO);
    
    // Recupera il profilo di un paziente specifico tramite ID (per medici)
    Response<PatientDTO> getPatientById(Long id);
}
