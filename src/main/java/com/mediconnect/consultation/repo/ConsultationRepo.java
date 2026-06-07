package com.mediconnect.consultation.repo;

import com.mediconnect.consultation.entity.Consultation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/*
Interfaccia che estende JpaRepository per gestire automaticamente le operazioni CRUD (salvataggio, ricerca, cancellazione) sulla tabella consultations.
Include metodi personalizzati per cercare referti per appuntamento e per paziente.
*/
public interface ConsultationRepo extends JpaRepository<Consultation, Long> {

    // Cerca il referto collegato a un appuntamento specifico (relazione OneToOne)
    Optional<Consultation> findByAppointmentId(Long appointmentId);

    // Cerca tutti i referti di un paziente tramite l'ID del paziente
    // nell'appuntamento collegato
    // Ordinati per data consulenza decrescente (dal più recente)
    List<Consultation> findByAppointmentPatientIdOrderByConsultationDateDesc(Long patientId);
}
