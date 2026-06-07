package com.mediconnect.medical.service;

import com.mediconnect.appointment.entity.Appointment;
import com.mediconnect.appointment.repo.AppointmentRepo;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.medical.dto.LabResultDTO;
import com.mediconnect.medical.dto.MedicalRecordDTO;
import com.mediconnect.medical.dto.PrescriptionDTO;
import com.mediconnect.medical.entity.LabResult;
import com.mediconnect.medical.entity.MedicalRecord;
import com.mediconnect.medical.entity.Prescription;
import com.mediconnect.medical.repo.LabResultRepo;
import com.mediconnect.medical.repo.MedicalRecordRepo;
import com.mediconnect.medical.repo.PrescriptionRepo;
import com.mediconnect.res.Response;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
// Servizio cartella clinica: creazione prescrizioni e esami, recupero storico tramite ereditarietà JOINED

@Service
@Transactional
@RequiredArgsConstructor
public class MedicalRecordService {

        // Repository per le cartelle cliniche
        private final MedicalRecordRepo medicalRecordRepo;
        // Repository per le prescrizioni
        private final PrescriptionRepo prescriptionRepo;
        // Repository per gli esami di laboratorio
        private final LabResultRepo labResultRepo;
        // Repository per gli appuntamenti
        private final AppointmentRepo appointmentRepo;
        // Servizio per la gestione utenti
        private final UserService userService;

        // Crea una nuova prescrizione farmaceutica collegata a un appuntamento
        public Response<PrescriptionDTO> createPrescription(PrescriptionDTO dto) {
                Appointment appointment = appointmentRepo.findById(dto.getAppointmentId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Appointment not found: " + dto.getAppointmentId()));

                Prescription prescription = Prescription.builder()
                                .appointment(appointment)
                                .notes(dto.getNotes())
                                .drugName(dto.getDrugName())
                                .dosage(dto.getDosage())
                                .frequency(dto.getFrequency())
                                .durationDays(dto.getDurationDays())
                                .validUntil(dto.getValidUntil())
                                .instructions(dto.getInstructions())
                                .build();

                Prescription saved = prescriptionRepo.save(prescription);
                return Response.<PrescriptionDTO>builder()
                                .statusCode(200).message("Prescription created successfully")
                                .data(toDto(saved)).build();
        }

        // Crea un nuovo risultato di laboratorio collegato a un appuntamento
        public Response<LabResultDTO> createLabResult(LabResultDTO dto) {
                Appointment appointment = appointmentRepo.findById(dto.getAppointmentId())
                                .orElseThrow(() -> new NotFoundException(
                                                "Appointment not found: " + dto.getAppointmentId()));

                LabResult labResult = LabResult.builder()
                                .appointment(appointment)
                                .notes(dto.getNotes())
                                .testName(dto.getTestName())
                                .resultValue(dto.getResultValue())
                                .unit(dto.getUnit())
                                .referenceRange(dto.getReferenceRange())
                                .isAbnormal(dto.getIsAbnormal() != null && dto.getIsAbnormal())
                                .labName(dto.getLabName())
                                .build();

                LabResult saved = labResultRepo.save(labResult);
                return Response.<LabResultDTO>builder()
                                .statusCode(200).message("Lab result created successfully")
                                .data(toDto(saved)).build();
        }

        // Recupera la timeline completa del paziente loggato (prescrizioni + esami
        // insieme)
        // Usa il polimorfismo JPA: la query restituisce MedicalRecord, il mapper
        // distingue il tipo
        public Response<List<MedicalRecordDTO>> getMyMedicalHistory() {
                User user = userService.getCurrentUser();
                List<MedicalRecord> records = medicalRecordRepo.findByPatientUserId(user.getId());
                List<MedicalRecordDTO> dtos = records.stream().map(this::toBaseDto).toList();
                return Response.<List<MedicalRecordDTO>>builder()
                                .statusCode(200).message("Medical history retrieved successfully")
                                .data(dtos).build();
        }

        // Recupera le prescrizioni di un appuntamento specifico
        public Response<List<PrescriptionDTO>> getPrescriptionsByAppointment(Long appointmentId) {
                List<PrescriptionDTO> dtos = prescriptionRepo.findByAppointmentId(appointmentId)
                                .stream().map(this::toDto).toList();
                return Response.<List<PrescriptionDTO>>builder()
                                .statusCode(200).message("Prescriptions retrieved successfully")
                                .data(dtos).build();
        }

        // Recupera gli esami di laboratorio di un appuntamento specifico
        public Response<List<LabResultDTO>> getLabResultsByAppointment(Long appointmentId) {
                List<LabResultDTO> dtos = labResultRepo.findByAppointmentId(appointmentId)
                                .stream().map(this::toDto).toList();
                return Response.<List<LabResultDTO>>builder()
                                .statusCode(200).message("Lab results retrieved successfully")
                                .data(dtos).build();
        }

        // Recupera tutte le prescrizioni del paziente loggato
        public Response<List<PrescriptionDTO>> getMyPrescriptions() {
                User user = userService.getCurrentUser();
                List<PrescriptionDTO> dtos = prescriptionRepo.findByPatientUserId(user.getId())
                                .stream().map(this::toDto).toList();
                return Response.<List<PrescriptionDTO>>builder()
                                .statusCode(200).message("Prescriptions retrieved successfully")
                                .data(dtos).build();
        }

        // Recupera tutti gli esami di laboratorio del paziente loggato
        public Response<List<LabResultDTO>> getMyLabResults() {
                User user = userService.getCurrentUser();
                List<LabResultDTO> dtos = labResultRepo.findByPatientUserId(user.getId())
                                .stream().map(this::toDto).toList();
                return Response.<List<LabResultDTO>>builder()
                                .statusCode(200).message("Lab results retrieved successfully")
                                .data(dtos).build();
        }

        // --- Mapper manuali (usati al posto di ModelMapper per gestire il
        // polimorfismo) ---

        // Converte Prescription entity → PrescriptionDTO
        private PrescriptionDTO toDto(Prescription p) {
                return PrescriptionDTO.builder()
                                .id(p.getId()).appointmentId(p.getAppointment().getId())
                                .createdAt(p.getCreatedAt()).notes(p.getNotes())
                                .drugName(p.getDrugName()).dosage(p.getDosage())
                                .frequency(p.getFrequency()).durationDays(p.getDurationDays())
                                .validUntil(p.getValidUntil()).instructions(p.getInstructions())
                                .build();
        }

        // Converte LabResult entity → LabResultDTO
        private LabResultDTO toDto(LabResult l) {
                return LabResultDTO.builder()
                                .id(l.getId()).appointmentId(l.getAppointment().getId())
                                .createdAt(l.getCreatedAt()).notes(l.getNotes())
                                .testName(l.getTestName()).resultValue(l.getResultValue())
                                .unit(l.getUnit()).referenceRange(l.getReferenceRange())
                                .isAbnormal(l.getIsAbnormal()).labName(l.getLabName())
                                .build();
        }

        // Converte MedicalRecord (polimorfico) → MedicalRecordDTO base con campo "type"
        // Usa instanceof per determinare se è una Prescription o un LabResult
        private MedicalRecordDTO toBaseDto(MedicalRecord mr) {
                String type = (mr instanceof Prescription) ? "PRESCRIPTION" : "LAB_RESULT";
                return MedicalRecordDTO.builder()
                                .id(mr.getId()).appointmentId(mr.getAppointment().getId())
                                .createdAt(mr.getCreatedAt()).notes(mr.getNotes())
                                .type(type).build();
        }
}
