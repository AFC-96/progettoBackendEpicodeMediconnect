package com.mediconnect.appointment.service;

import com.mediconnect.appointment.dto.AppointmentDTO;
import com.mediconnect.appointment.entity.Appointment;
import com.mediconnect.appointment.repo.AppointmentRepo;
import com.mediconnect.doctor.entity.Doctor;
import com.mediconnect.doctor.repo.DoctorRepo;
import com.mediconnect.enums.AppointmentStatus;
import com.mediconnect.exceptions.BadRequestException;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.notification.dto.NotificationDTO;
import com.mediconnect.notification.service.NotificationService;
import com.mediconnect.patient.entity.Patient;
import com.mediconnect.patient.repo.PatientRepo;
import com.mediconnect.res.Response;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
// Implementazione servizio appuntamenti: prenotazione con controllo conflitti, cancellazione con notifica, recupero per ruolo

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService {

        // Repository per gli appuntamenti
        private final AppointmentRepo appointmentRepo;
        // Repository per i profili medici
        private final DoctorRepo doctorRepo;
        // Repository per i profili pazienti
        private final PatientRepo patientRepo;
        // Servizio per la gestione utenti
        private final UserService userService;
        // Servizio per l'invio delle notifiche email
        private final NotificationService notificationService;
        // Mapper per la conversione entità-DTO
        private final ModelMapper modelMapper;

        // Prenota un nuovo appuntamento verificando disponibilità e inviando notifiche
        @Override
        public Response<AppointmentDTO> bookAppointment(AppointmentDTO dto) {
                User currentUser = userService.getCurrentUser();
                Patient patient = patientRepo.findByUser(currentUser)
                                .orElseThrow(() -> new NotFoundException("Patient profile not found"));
                Doctor doctor = doctorRepo.findById(dto.getDoctorId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Doctor not found with ID: " + dto.getDoctorId()));

                var endTime = dto.getEndTime() != null ? dto.getEndTime() : dto.getStartTime().plusHours(1);
                if (!appointmentRepo.findConflictingAppointments(doctor.getId(), dto.getStartTime(), endTime)
                                .isEmpty()) {
                        throw new BadRequestException("Doctor not available at this time. Please choose another slot.");
                }

                Appointment appointment = Appointment.builder()
                                .startTime(dto.getStartTime()).endTime(endTime)
                                .purposeOfConsultation(dto.getPurposeOfConsultation())
                                .initialSymptoms(dto.getInitialSymptoms())
                                .status(AppointmentStatus.SCHEDULED)
                                .doctor(doctor).patient(patient).build();

                Appointment saved = appointmentRepo.save(appointment);
                log.info("Appointment booked: patient={} doctor={}", patient.getId(), doctor.getId());

                // Notifica il paziente
                notificationService.sendEmail(NotificationDTO.builder()
                                .recipient(currentUser.getEmail()).subject("Appointment Confirmed")
                                .templateName("patient-appointment")
                                .templateVariables(Map.of("name", currentUser.getName(),
                                                "startTime", saved.getStartTime().toString(),
                                                "doctorName",
                                                doctor.getUser() != null ? doctor.getUser().getName() : "Your Doctor"))
                                .build(), currentUser);

                // Notifica il medico
                if (doctor.getUser() != null) {
                        notificationService.sendEmail(NotificationDTO.builder()
                                        .recipient(doctor.getUser().getEmail()).subject("New Appointment Scheduled")
                                        .templateName("doctor-appointment")
                                        .templateVariables(Map.of("name", doctor.getUser().getName(),
                                                        "startTime", saved.getStartTime().toString(),
                                                        "patientName", currentUser.getName()))
                                        .build(), doctor.getUser());
                }

                return Response.<AppointmentDTO>builder().statusCode(200)
                                .message("Appointment booked successfully")
                                .data(modelMapper.map(saved, AppointmentDTO.class)).build();
        }

        // Recupera gli appuntamenti dell'utente corrente
        @Override
        public Response<List<AppointmentDTO>> getMyAppointments() {
                User user = userService.getCurrentUser();
                boolean isDoctor = user.getRoles().stream().anyMatch(r -> r.getName().equals("DOCTOR"));
                List<Appointment> list = isDoctor
                                ? appointmentRepo.findByDoctor_User_IdOrderByIdDesc(user.getId())
                                : appointmentRepo.findByPatient_User_IdOrderByIdDesc(user.getId());
                return Response.<List<AppointmentDTO>>builder().statusCode(200)
                                .message("Appointments retrieved successfully")
                                .data(list.stream().map(a -> modelMapper.map(a, AppointmentDTO.class)).toList())
                                .build();
        }

        // Cerca un appuntamento specifico per ID
        @Override
        public Response<AppointmentDTO> getAppointmentById(Long id) {
                Appointment a = appointmentRepo.findById(id)
                                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id));
                return Response.<AppointmentDTO>builder().statusCode(200)
                                .message("Appointment retrieved successfully")
                                .data(modelMapper.map(a, AppointmentDTO.class)).build();
        }

        // Annulla il record e invia la notifica
        @Override
        public Response<?> cancelAppointment(Long id) {
                Appointment appointment = appointmentRepo.findById(id)
                                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id));
                if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
                        throw new BadRequestException("Appointment is already cancelled");
                }
                appointment.setStatus(AppointmentStatus.CANCELLED);
                appointmentRepo.save(appointment);

                User patientUser = appointment.getPatient().getUser();
                if (patientUser != null) {
                        notificationService.sendEmail(NotificationDTO.builder()
                                        .recipient(patientUser.getEmail()).subject("Appointment Cancelled")
                                        .templateName("appointment-cancellation")
                                        .templateVariables(Map.of("name", patientUser.getName(),
                                                        "startTime", appointment.getStartTime().toString()))
                                        .build(), patientUser);
                }
                return Response.builder().statusCode(200).message("Appointment cancelled successfully").build();
        }

        // Aggiorna i dati di un record esistente
        @Override
        public Response<AppointmentDTO> updateAppointmentStatus(Long id, AppointmentStatus status) {
                Appointment appointment = appointmentRepo.findById(id)
                                .orElseThrow(() -> new NotFoundException("Appointment not found with ID: " + id));
                appointment.setStatus(status);
                Appointment updated = appointmentRepo.save(appointment);
                return Response.<AppointmentDTO>builder().statusCode(200)
                                .message("Status updated successfully")
                                .data(modelMapper.map(updated, AppointmentDTO.class)).build();
        }
}
