package com.mediconnect.medical.repo;

import com.mediconnect.medical.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/*
 Interfaccia che estende JpaRepository per la tabella base "medical_records".
 Grazie al polimorfismo JPA (ereditarietà JOINED), le query qui restituiscono
 sia Prescription che LabResult come oggetti MedicalRecord.
 */
public interface MedicalRecordRepo extends JpaRepository<MedicalRecord, Long> {

    // Cerca tutti i record clinici (prescrizioni + esami) di un appuntamento
    List<MedicalRecord> findByAppointmentId(Long appointmentId);

    // Cerca tutti i record clinici di un paziente tramite l'ID utente (query JPQL)
    // Ordinati per data di creazione decrescente (timeline dal più recente)
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.patient.user.id = :userId ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByPatientUserId(@Param("userId") Long userId);

    // Cerca tutti i record clinici creati da un medico tramite l'ID utente
    @Query("SELECT mr FROM MedicalRecord mr WHERE mr.appointment.doctor.user.id = :userId ORDER BY mr.createdAt DESC")
    List<MedicalRecord> findByDoctorUserId(@Param("userId") Long userId);
}
