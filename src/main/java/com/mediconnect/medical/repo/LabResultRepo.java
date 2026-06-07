package com.mediconnect.medical.repo;

import com.mediconnect.medical.entity.LabResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
 Interfaccia che estende JpaRepository per la tabella "lab_results".
 Opera sulla sotto-classe LabResult (ereditarietà JOINED da MedicalRecord).
 Include query per cercare esami per appuntamento, paziente e risultati anomali.
 */
public interface LabResultRepo extends JpaRepository<LabResult, Long> {

    // Cerca gli esami di laboratorio di un appuntamento specifico
    List<LabResult> findByAppointmentId(Long appointmentId);

    // Cerca tutti gli esami di un paziente tramite l'ID utente (ordinate dal più recente)
    @Query("SELECT l FROM LabResult l WHERE l.appointment.patient.user.id = :patientUserId ORDER BY l.createdAt DESC")
    List<LabResult> findByPatientUserId(@Param("patientUserId") Long patientUserId);

    // Cerca tutti gli esami con risultati anomali (flag isAbnormal = true)
    @Query("SELECT l FROM LabResult l WHERE l.isAbnormal = true ORDER BY l.createdAt DESC")
    List<LabResult> findAllAbnormal();
}
