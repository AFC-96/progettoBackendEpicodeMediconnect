package com.mediconnect.patient.service;

import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.patient.dto.PatientDTO;
import com.mediconnect.patient.entity.Patient;
import com.mediconnect.patient.repo.PatientRepo;
import com.mediconnect.res.Response;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/*
 Implementazione del servizio pazienti.
 Gestisce il recupero e l'aggiornamento dei dati (anagrafici e clinici) dei pazienti.
 Tutte le operazioni che modificano il profilo ("me") usano il contesto di sicurezza (UserService)
 per garantire che un paziente possa modificare solo i propri dati.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PatientServiceImpl implements PatientService {

    // Repository per l'accesso ai dati del paziente
    private final PatientRepo patientRepo;
    // Servizio per recuperare l'utente attualmente loggato tramite SecurityContext
    private final UserService userService;
    // Mapper per convertire automaticamente le Entity in DTO e viceversa
    private final ModelMapper modelMapper;

    // Recupera i dati clinici e anagrafici dell'utente attualmente loggato
    @Override
    public Response<PatientDTO> getMyProfile() {
        // 1. Recupera l'utente dal SecurityContext (token JWT)
        User user = userService.getCurrentUser();
        
        // 2. Cerca il record Patient associato a questo utente
        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                
        // 3. Converte in DTO e restituisce
        return Response.<PatientDTO>builder().statusCode(200)
                .message("Profile retrieved successfully")
                .data(modelMapper.map(patient, PatientDTO.class)).build();
    }

    // Aggiorna in modo parziale (PATCH-like) i dati del paziente loggato
    @Override
    public Response<PatientDTO> updateMyProfile(PatientDTO dto) {
        // 1. Identifica il paziente tramite il token di sicurezza
        User user = userService.getCurrentUser();
        Patient patient = patientRepo.findByUser(user)
                .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                
        // 2. Aggiorna solo i campi effettivamente inviati nella richiesta (evita di sovrascrivere con null)
        if (dto.getFirstName() != null) patient.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) patient.setLastName(dto.getLastName());
        if (dto.getDateOfBirth() != null) patient.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getPhone() != null) patient.setPhone(dto.getPhone());
        if (dto.getKnownAllergies() != null) patient.setKnownAllergies(dto.getKnownAllergies());
        if (dto.getBloodGroup() != null) patient.setBloodGroup(dto.getBloodGroup());
        if (dto.getGenotype() != null) patient.setGenotype(dto.getGenotype());
        
        // 3. Salva nel database (Spring Data JPA capisce che è un UPDATE perché l'ID esiste)
        Patient updated = patientRepo.save(patient);
        
        // 4. Converte e restituisce
        return Response.<PatientDTO>builder().statusCode(200)
                .message("Profile updated successfully")
                .data(modelMapper.map(updated, PatientDTO.class)).build();
    }

    // Recupera il profilo completo di un paziente specifico (usato dai medici)
    @Override
    public Response<PatientDTO> getPatientById(Long id) {
        // 1. Cerca il paziente per ID direttamente nel DB
        Patient patient = patientRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("Patient not found with ID: " + id));
                
        // 2. Converte in DTO e restituisce
        return Response.<PatientDTO>builder().statusCode(200)
                .message("Patient retrieved successfully")
                .data(modelMapper.map(patient, PatientDTO.class)).build();
    }
}
