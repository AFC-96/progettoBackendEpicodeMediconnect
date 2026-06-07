package com.mediconnect.appointment.service;

import com.mediconnect.appointment.dto.AppointmentDTO;
import com.mediconnect.enums.AppointmentStatus;
import com.mediconnect.res.Response;

import java.util.List;

/* Interfaccia servizio appuntamenti: prenotazione, cancellazione, aggiornamento e recupero. 
Il suo scopo principale è definire quali operazioni sono possibili nell'applicazione, senza rivelare i dettagli complessi su come vengono eseguite. */
public interface AppointmentService {
    Response<AppointmentDTO> bookAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<AppointmentDTO> getAppointmentById(Long id);

    Response<?> cancelAppointment(Long id);

    Response<AppointmentDTO> updateAppointmentStatus(Long id, AppointmentStatus status);
}
