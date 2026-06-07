package com.mediconnect.consultation.service;

import com.mediconnect.appointment.entity.Appointment;
import com.mediconnect.appointment.repo.AppointmentRepo;
import com.mediconnect.consultation.dto.ConsultationDTO;
import com.mediconnect.consultation.entity.Consultation;
import com.mediconnect.consultation.repo.ConsultationRepo;
import com.mediconnect.exceptions.BadRequestException;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.res.Response;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
// Implementazione servizio consulti: creazione referto SOAP e recupero per appuntamento

@Service
@Transactional
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService {

        // Repository per i referti clinici
        private final ConsultationRepo consultationRepo;
        // Repository per gli appuntamenti
        private final AppointmentRepo appointmentRepo;
        // Servizio per la gestione utenti
        private final UserService userService;
        // Mapper per la conversione entità-DTO
        private final ModelMapper modelMapper;

        // Crea e salva un nuovo record nel database
        @Override
        public Response<ConsultationDTO> createConsultation(ConsultationDTO dto) {
                Appointment appointment = appointmentRepo.findById(dto.getAppointmentId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Appointment not found with ID: " + dto.getAppointmentId()));
                if (consultationRepo.findByAppointmentId(appointment.getId()).isPresent()) {
                        throw new BadRequestException("A consultation already exists for this appointment");
                }
                Consultation consultation = Consultation.builder()
                                .appointment(appointment).consultationDate(LocalDateTime.now())
                                .subjectiveNotes(dto.getSubjectiveNotes()).objectiveFindings(dto.getObjectiveFindings())
                                .assessment(dto.getAssessment()).plan(dto.getPlan()).build();
                Consultation saved = consultationRepo.save(consultation);
                return Response.<ConsultationDTO>builder().statusCode(200)
                                .message("Consultation created successfully")
                                .data(modelMapper.map(saved, ConsultationDTO.class)).build();
        }

        // Recupera i dati richiesti dal database
        @Override
        public Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {
                Consultation c = consultationRepo.findByAppointmentId(appointmentId)
                                .orElseThrow(() -> new NotFoundException(
                                                "Consultation not found for appointment ID: " + appointmentId));
                return Response.<ConsultationDTO>builder().statusCode(200)
                                .message("Consultation retrieved successfully")
                                .data(modelMapper.map(c, ConsultationDTO.class)).build();
        }

        // Recupera i dati dell'utente autenticato
        @Override
        public Response<List<ConsultationDTO>> getMyConsultations() {
                User user = userService.getCurrentUser();
                List<ConsultationDTO> list = consultationRepo
                                .findByAppointmentPatientIdOrderByConsultationDateDesc(user.getId()).stream()
                                .map(c -> modelMapper.map(c, ConsultationDTO.class)).toList();
                return Response.<List<ConsultationDTO>>builder().statusCode(200)
                                .message("Consultations retrieved successfully").data(list).build();
        }

        // Aggiorna i dati di un record esistente
        @Override
        public Response<ConsultationDTO> updateConsultation(Long id, ConsultationDTO dto) {
                Consultation c = consultationRepo.findById(id)
                                .orElseThrow(() -> new NotFoundException("Consultation not found with ID: " + id));
                if (dto.getSubjectiveNotes() != null)
                        c.setSubjectiveNotes(dto.getSubjectiveNotes());
                if (dto.getObjectiveFindings() != null)
                        c.setObjectiveFindings(dto.getObjectiveFindings());
                if (dto.getAssessment() != null)
                        c.setAssessment(dto.getAssessment());
                if (dto.getPlan() != null)
                        c.setPlan(dto.getPlan());
                Consultation updated = consultationRepo.save(c);
                return Response.<ConsultationDTO>builder().statusCode(200)
                                .message("Consultation updated successfully")
                                .data(modelMapper.map(updated, ConsultationDTO.class)).build();
        }
}
