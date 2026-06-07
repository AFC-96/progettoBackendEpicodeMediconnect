package com.mediconnect.users.service;

import com.mediconnect.res.Response;
import com.mediconnect.users.dto.UpdatePasswordRequest;
import com.mediconnect.users.dto.UserDTO;
import com.mediconnect.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/*
 Interfaccia che definisce il contratto per le operazioni di gestione account.
 Si occupa del recupero utente corrente, aggiornamento password e gestione file (avatar).
 */
public interface UserService {

    // Recupera l'entità User dell'utente correntemente autenticato tramite SecurityContext
    User getCurrentUser();

    // Recupera i dati dell'utente autenticato incapsulati in un DTO
    Response<UserDTO> getMyUserDetails();

    // Recupera i dati di un utente specifico (uso Admin)
    Response<UserDTO> getUserById(Long userId);

    // Recupera tutti gli utenti (uso Admin)
    Response<List<UserDTO>> getAllUsers();

    // Modifica la password dell'utente autenticato (verifica vecchia password)
    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);

    // Salva l'immagine del profilo sul file system locale e aggiorna l'URL nel DB
    Response<?> uploadProfilePicture(MultipartFile file);

    // (Stub) Metodo predisposto per l'upload su Amazon S3
    Response<?> uploadProfilePictureToS3(MultipartFile file);
}
