package com.mediconnect.appointment.repo;

import com.mediconnect.appointment.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

/*
Interfaccia che estende JpaRepository per gestire automaticamente le operazioni CRUD(salvataggio, ricerca, cancellazione) nel database.
Include anche metodi personalizzati per cercare per medico/paziente e verificare conflitti orari.
 */

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {

   // Cerca gli appuntamenti di un medico tramite l'ID utente, ordinati dal più
   // recent
   List<Appointment> findByDoctor_User_IdOrderByIdDesc(Long userId);

   // Cerca per patient.user.id, ordinati dal più recente
   List<Appointment> findByPatient_User_IdOrderByIdDesc(Long userId);

   /*
    * Query JPQL personalizzata per trovare conflitti orari: filtra per lo stesso
    * medico,
    * considera solo gli appuntamenti SCHEDULED, e verifica la sovrapposizione
    * temporale.
    * Se restituisce una lista NON vuota, il Service blocca la prenotazione.
    */
   @Query("SELECT a FROM Appointment a " +
         "WHERE a.doctor.id = :doctorId " +
         "AND a.status = 'SCHEDULED' " +
         "AND (" +
         "    (a.startTime < :newEndTime AND a.endTime > :newStartTime)" +
         ")")
   List<Appointment> findConflictingAppointments(
         @Param("doctorId") Long doctorId,
         @Param("newStartTime") LocalDateTime newStartTime,
         @Param("newEndTime") LocalDateTime newEndTime);

}
