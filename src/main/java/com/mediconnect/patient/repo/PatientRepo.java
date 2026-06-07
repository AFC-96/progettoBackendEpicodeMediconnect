package com.mediconnect.patient.repo;

import com.mediconnect.patient.entity.Patient;
import com.mediconnect.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/*
 Interfaccia che estende JpaRepository per gestire l'accesso alla tabella "patients".
 Include un metodo personalizzato per recuperare il record del paziente 
 partendo dall'entità User associata (usato durante le chiamate "getMyProfile").
 */
public interface PatientRepo extends JpaRepository<Patient, Long> {

    // Recupera il profilo paziente cercando tramite l'utente (relazione OneToOne)
    Optional<Patient> findByUser(User user);
}