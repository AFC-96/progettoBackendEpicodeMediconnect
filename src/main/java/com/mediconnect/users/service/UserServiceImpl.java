package com.mediconnect.users.service;

import com.mediconnect.exceptions.BadRequestException;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.notification.dto.NotificationDTO;
import com.mediconnect.notification.service.NotificationService;
import com.mediconnect.res.Response;
import com.mediconnect.users.dto.UpdatePasswordRequest;
import com.mediconnect.users.dto.UserDTO;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/*
 Implementazione del servizio utenti.
 Questa classe gestisce principalmente le operazioni sull'account dell'utente connesso,
 estraendone in modo sicuro l'identità (email) dal SecurityContext di Spring (JWT).
 Gestisce la modifica sicura della password e il caricamento dell'immagine profilo
 salvandola direttamente nella cartella 'public' del frontend React.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    // Repository per gli utenti
    private final UserRepo userRepo;
    // Mapper per la conversione entità-DTO
    private final ModelMapper modelMapper;
    // Encoder per la cifratura delle password con BCrypt
    private final PasswordEncoder passwordEncoder;
    // Servizio per l'invio delle notifiche email
    private final NotificationService notificationService;

    // private final String uploadDir = "uploads/profile-pictures/"; // Percorso
    // backend per il salvataggio immagini

    // Dipendenza: String
    private final String uploadDir = "/Users/mac/phegonDev/dat-react/public/profile-picture/"; // Percorso frontend per
                                                                                               // il salvataggio
                                                                                               // immagini

    // Recupera l'entità dell'utente che ha effettuato l'attuale richiesta HTTP
    @Override
    public User getCurrentUser() {
        // 1. Estrae l'autenticazione dal contesto di Spring Security (popolato da
        // AuthFilter)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new NotFoundException("User is not authenticated");
        }

        // 2. Il "name" nel nostro caso corrisponde all'email inserita nel token JWT
        String email = authentication.getName();

        // 3. Recupera l'utente dal database
        return userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("User Not Found"));
    }

    // Recupera i dati del proprio profilo e li converte in DTO per il frontend
    @Override
    public Response<UserDTO> getMyUserDetails() {

        User user = getCurrentUser();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(200)
                .message("User details retrieved successfully.")
                .data(userDTO)
                .build();

    }

    // Recupera i dati richiesti dal database
    @Override
    public Response<UserDTO> getUserById(Long userId) {

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found with ID: " + userId));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(200)
                .message("User details retrieved successfully.")
                .data(userDTO)
                .build();
    }

    // Recupera l'elenco completo di tutti i record
    @Override
    public Response<List<UserDTO>> getAllUsers() {

        List<UserDTO> userDTOS = userRepo.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(200)
                .message("All users retrieved successfully.")
                .data(userDTOS)
                .build();
    }

    // Modifica la password verificando prima quella vecchia
    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {
        // 1. Identifica l'utente connesso
        User user = getCurrentUser();

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if (oldPassword == null || newPassword == null) {
            throw new BadRequestException("Old and New Password Required");
        }

        // 2. Valida la vecchia password confrontando l'hash BCrypt
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old Password not Correct");
        }

        // 3. Salva la nuova password crittografata
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);

        // 4. Invia email di conferma cambio password asincrona
        NotificationDTO notificationDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Your Password Was Successfully Changed")
                .templateName("password-change")
                .templateVariables(Map.of("name", user.getName()))
                .build();
        notificationService.sendEmail(notificationDTO, user);

        return Response.builder()
                .statusCode(200)
                .message("Password Changed Successfully")
                .build();
    }

    // Carica e salva l'immagine profilo direttamente nella cartella del frontend
    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentUser();

        try {
            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                Path oldFile = Paths.get(user.getProfilePictureUrl());
                if (Files.exists(oldFile)) {
                    Files.delete(oldFile);
                }
            }

            // Genera un nome file univoco
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);

            // String fileUrl = uploadDir + newFileName;
            String fileUrl = "/profile-picture/" + newFileName;

            user.setProfilePictureUrl(fileUrl);
            userRepo.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully.")
                    .data(fileUrl)
                    .build();

        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    // Carica e salva il file sul server
    @Override
    public Response<?> uploadProfilePictureToS3(MultipartFile file) {
        return null;
    }
}
