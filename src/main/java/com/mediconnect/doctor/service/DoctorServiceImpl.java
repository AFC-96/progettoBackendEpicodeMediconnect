package com.mediconnect.doctor.service;

import com.mediconnect.doctor.dto.DoctorDTO;
import com.mediconnect.doctor.entity.Doctor;
import com.mediconnect.doctor.repo.DoctorRepo;
import com.mediconnect.enums.Specialization;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.res.Response;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Implementazione servizio medici: recupero, ricerca per specializzazione, aggiornamento
@Service
@Transactional
@RequiredArgsConstructor
public class DoctorServiceImpl implements DoctorService {

        // Repository per i profili medici
        private final DoctorRepo doctorRepo;
        // Servizio per la gestione utenti
        private final UserService userService;
        // Mapper per la conversione entità-DTO
        private final ModelMapper modelMapper;

        // Recupera l'elenco completo di tutti i record
        @Override
        public Response<List<DoctorDTO>> getAllDoctors() {
                List<DoctorDTO> doctors = doctorRepo.findAll().stream()
                                .map(d -> modelMapper.map(d, DoctorDTO.class)).toList();
                return Response.<List<DoctorDTO>>builder().statusCode(200)
                                .message("Doctors retrieved successfully").data(doctors).build();
        }

        // Recupera i dati richiesti dal database
        @Override
        public Response<DoctorDTO> getDoctorById(Long id) {
                Doctor doctor = doctorRepo.findById(id)
                                .orElseThrow(() -> new NotFoundException("Doctor not found with ID: " + id));
                return Response.<DoctorDTO>builder().statusCode(200)
                                .message("Doctor retrieved successfully")
                                .data(modelMapper.map(doctor, DoctorDTO.class)).build();
        }

        // Recupera i dati richiesti dal database
        @Override
        public Response<List<DoctorDTO>> getDoctorsBySpecialization(Specialization specialization) {
                List<DoctorDTO> doctors = doctorRepo.findBySpecialization(specialization).stream()
                                .map(d -> modelMapper.map(d, DoctorDTO.class)).toList();
                return Response.<List<DoctorDTO>>builder().statusCode(200)
                                .message("Doctors retrieved successfully").data(doctors).build();
        }

        // Recupera i dati dell'utente autenticato
        @Override
        public Response<DoctorDTO> getMyProfile() {
                User user = userService.getCurrentUser();
                Doctor doctor = doctorRepo.findByUser(user)
                                .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
                return Response.<DoctorDTO>builder().statusCode(200)
                                .message("Profile retrieved successfully")
                                .data(modelMapper.map(doctor, DoctorDTO.class)).build();
        }

        // Aggiorna i dati di un record esistente
        @Override
        public Response<DoctorDTO> updateMyProfile(DoctorDTO dto) {
                User user = userService.getCurrentUser();
                Doctor doctor = doctorRepo.findByUser(user)
                                .orElseThrow(() -> new NotFoundException("Doctor profile not found"));
                if (dto.getFirstName() != null)
                        doctor.setFirstName(dto.getFirstName());
                if (dto.getLastName() != null)
                        doctor.setLastName(dto.getLastName());
                if (dto.getSpecialization() != null)
                        doctor.setSpecialization(dto.getSpecialization());
                Doctor updated = doctorRepo.save(doctor);
                return Response.<DoctorDTO>builder().statusCode(200)
                                .message("Profile updated successfully")
                                .data(modelMapper.map(updated, DoctorDTO.class)).build();
        }
}
