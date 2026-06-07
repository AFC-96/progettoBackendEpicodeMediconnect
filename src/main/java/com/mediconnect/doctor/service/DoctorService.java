package com.mediconnect.doctor.service;

import com.mediconnect.doctor.dto.DoctorDTO;
import com.mediconnect.enums.Specialization;
import com.mediconnect.res.Response;

import java.util.List;

/*
Interfaccia che definisce il contratto del servizio medici.
Dichiara le operazioni disponibili senza esporre i dettagli implementativi, permettendo al Controller di dipendere dall'interfaccia e non dall'implementazione.
*/
public interface DoctorService {
    // Recupera la lista di tutti i medici registrati
    Response<List<DoctorDTO>> getAllDoctors();

    // Recupera un singolo medico per ID
    Response<DoctorDTO> getDoctorById(Long id);

    // Filtra i medici per specializzazione
    Response<List<DoctorDTO>> getDoctorsBySpecialization(Specialization specialization);

    // Recupera il profilo del medico loggato
    Response<DoctorDTO> getMyProfile();

    // Aggiorna il profilo del medico loggato
    Response<DoctorDTO> updateMyProfile(DoctorDTO doctorDTO);
}
