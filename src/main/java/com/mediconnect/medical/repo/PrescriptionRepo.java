package com.mediconnect.medical.repo;

import com.mediconnect.medical.entity.Prescription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
 Interfaccia che estende JpaRepository per la tabella "prescriptions".
 Opera sulla sotto-classe Prescription (ereditarietà JOINED da MedicalRecord).
 Include query per cercare prescrizioni per appuntamento, paziente e nome farmaco.
 */
public interface PrescriptionRepo extends JpaRepository<Prescription, Long> {

    // Cerca le prescrizioni di un appuntamento specifico
    List<Prescription> findByAppointmentId(Long appointmentId);

    // Cerca tutte le prescrizioni di un paziente tramite l'ID utente (ordinate dal più recente)
    @Query("SELECT p FROM Prescription p WHERE p.appointment.patient.user.id = :patientUserId ORDER BY p.createdAt DESC")
    List<Prescription> findByPatientUserId(@Param("patientUserId") Long patientUserId);

    // Cerca prescrizioni per nome farmaco (case-insensitive, ricerca parziale con LIKE)
    @Query("SELECT p FROM Prescription p WHERE LOWER(p.drugName) LIKE LOWER(CONCAT('%', :drugName, '%'))")
    List<Prescription> findByDrugNameContainingIgnoreCase(@Param("drugName") String drugName);
}
