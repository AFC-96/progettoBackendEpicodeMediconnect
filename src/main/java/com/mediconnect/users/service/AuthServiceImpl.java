package com.mediconnect.users.service;

import com.mediconnect.doctor.entity.Doctor;
import com.mediconnect.doctor.repo.DoctorRepo;
import com.mediconnect.exceptions.BadRequestException;
import com.mediconnect.exceptions.NotFoundException;
import com.mediconnect.notification.dto.NotificationDTO;
import com.mediconnect.notification.service.NotificationService;
import com.mediconnect.patient.entity.Patient;
import com.mediconnect.patient.repo.PatientRepo;
import com.mediconnect.res.Response;
import com.mediconnect.role.entity.Role;
import com.mediconnect.role.repo.RoleRepo;
import com.mediconnect.security.JwtService;
import com.mediconnect.users.dto.LoginRequest;
import com.mediconnect.users.dto.LoginResponse;
import com.mediconnect.users.dto.RegistrationRequest;
import com.mediconnect.users.dto.ResetPasswordRequest;
import com.mediconnect.users.entity.PasswordResetCode;
import com.mediconnect.users.entity.User;
import com.mediconnect.users.repo.PasswordResetRepo;
import com.mediconnect.users.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/*
 Implementazione del servizio di Autenticazione.
 Esegue 3 grandi compiti:
 1. REGISTRAZIONE: valida l'utente, cifra la password, crea l'entità User e, 
    in base al ruolo, crea dinamicamente il profilo Patient o Doctor collegato.
 2. LOGIN: valida email e password ed emette un JWT firmato.
 3. RESET PASSWORD: genera token univoci salvati nel DB, invia i link via email, 
    verifica i codici (controllo scadenza) e aggiorna la password.
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

        // Repository per gli utenti
        private final UserRepo userRepo;
        // Repository per i ruoli
        private final RoleRepo roleRepo;
        // Encoder per la cifratura delle password con BCrypt
        private final PasswordEncoder passwordEncoder;
        // Servizio per generazione e validazione token JWT
        private final JwtService jwtService;
        // Servizio per l'invio delle notifiche email
        private final NotificationService notificationService;

        // Repository per i profili pazienti
        private final PatientRepo patientRepo;
        // Repository per i profili medici
        private final DoctorRepo doctorRepo;

        // Repository per i codici di reset password
        private final PasswordResetRepo passwordResetRepo;
        // Generatore di codici univoci per reset password
        private final CodeGenerator codeGenerator;

        @Value("${password.reset.link}")
        private String resetLink;

        @Value("${login.link}")
        private String loginLink;

        // Registra un nuovo utente creando account e relativo profilo (Paziente/Medico)
        @Override
        public Response<String> register(RegistrationRequest request) {
                // STEP 1: Verifica che l'email non sia già in uso (Duplicati vietati)
                if (userRepo.findByEmail(request.getEmail()).isPresent()) {
                        throw new BadRequestException("User with email already exists");
                }

                // Determina i ruoli. Default: PATIENT
                List<String> requestedRoleNames = (request.getRoles() != null && !request.getRoles().isEmpty())
                                ? request.getRoles().stream().map(String::toUpperCase).toList()
                                : List.of("PATIENT");

                boolean isDoctor = requestedRoleNames.contains("DOCTOR");

                if (isDoctor && (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank())) {
                        throw new BadRequestException("License number required to register a doctor.");
                }

                // STEP 2: Carica e valida i ruoli dal database (da stringhe a entità Role)
                List<Role> roles = requestedRoleNames.stream()
                                .map(roleRepo::findByName)
                                .flatMap(Optional::stream)
                                .toList();

                if (roles.isEmpty()) {
                        throw new NotFoundException(
                                        "Registration failed: Requested roles were not found in the database.");
                }

                // STEP 3: Crea e salva la nuova entità utente radice (con password cifrata)
                User newUser = User.builder()
                                .email(request.getEmail())
                                .password(passwordEncoder.encode(request.getPassword()))
                                .name(request.getName())
                                .roles(roles)
                                .build();

                User savedUser = userRepo.save(newUser);

                log.info("New user registered: {} with {} roles.", savedUser.getEmail(), roles.size());

                // STEP 4: Crea il profilo specifico (sotto-tabella) in base al ruolo assegnato
                for (Role role : roles) {
                        String roleName = role.getName();

                        switch (roleName) {
                                case "PATIENT":
                                        createPatientProfile(savedUser);
                                        log.info("Patient profile created: {}", savedUser.getEmail());
                                        break;

                                case "DOCTOR":
                                        createDoctorProfile(request, savedUser);
                                        log.info("Doctor profile created: {}", savedUser.getEmail());
                                        break;

                                case "ADMIN":
                                        log.info("Admin role assigned to user: {}", savedUser.getEmail());
                                        break;

                                default:
                                        log.warn("Assigned role '{}' has no corresponding profile creation logic.",
                                                        roleName);
                                        break;
                        }
                }

                // STEP 5: Invia email di benvenuto asincrona tramite NotificationService
                sendRegistrationEmail(request, savedUser);

                // STEP 6: Restituisce la risposta HTTP 200 di successo
                return Response.<String>builder()
                                .statusCode(200)
                                .message("Registration successful. A welcome email has been sent to you.")
                                .data(savedUser.getEmail())
                                .build();

        }

        // Autentica l'utente e genera il token JWT
        @Override
        public Response<LoginResponse> login(LoginRequest loginRequest) {
                String email = loginRequest.getEmail();
                String password = loginRequest.getPassword();

                // 1. Verifica che l'email esista
                User user = userRepo.findByEmail(email).orElseThrow(() -> new NotFoundException("Email Not Found"));

                // 2. Valida la password confrontandola con l'hash BCrypt salvato
                if (!passwordEncoder.matches(password, user.getPassword())) {
                        throw new BadRequestException("Password doesn't match");
                }

                // 3. Genera un token JWT (valido per la durata configurata in properties)
                String token = jwtService.generateToken(user.getEmail());

                LoginResponse loginResponse = LoginResponse.builder()
                                .roles(user.getRoles().stream().map(Role::getName).toList())
                                .token(token)
                                .build();

                return Response.<LoginResponse>builder()
                                .statusCode(200)
                                .message("Login Successful")
                                .data(loginResponse)
                                .build();

        }

        // Gestisce la prima fase del reset password: genera codice e invia email
        @Override
        public Response<?> forgetPassword(String email) {
                // 1. Cerca l'utente
                User user = userRepo.findByEmail(email)
                                .orElseThrow(() -> new NotFoundException("User Not Found"));

                // 2. Cancella eventuali vecchi codici di reset pendenti (per evitare
                // spam/abusi)
                passwordResetRepo.deleteByUserId(user.getId());

                // 3. Genera un nuovo codice segreto alfanumerico
                String code = codeGenerator.generateUniqueCode();
                PasswordResetCode resetCode = PasswordResetCode.builder()
                                .user(user)
                                .code(code)
                                .expiryDate(calculateExpiryDate()) // Scade tra tot ore
                                .used(false)
                                .build();
                passwordResetRepo.save(resetCode);

                // 4. Invia l'email con il link (che include il codice nell'URL)
                NotificationDTO passwordResetEmail = NotificationDTO.builder()
                                .recipient(user.getEmail())
                                .subject("Password Reset Code")
                                .templateName("password-reset")
                                .templateVariables(Map.of(
                                                "name", user.getName(),
                                                "resetLink", resetLink + code))
                                .build();

                notificationService.sendEmail(passwordResetEmail, user);

                return Response.builder()
                                .statusCode(200)
                                .message("Password reset code sent to your email")
                                .build();
        }

        // Seconda fase: convalida il codice e applica la nuova password
        @Override
        public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
                String code = resetPasswordRequest.getCode();
                String newPassword = resetPasswordRequest.getNewPassword();

                // 1. Cerca il record del codice di reset nel DB
                PasswordResetCode resetCode = passwordResetRepo.findByCode(code)
                                .orElseThrow(() -> new BadRequestException("Invalid reset code"));

                // 2. Controlla che non sia scaduto
                if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())) {
                        passwordResetRepo.delete(resetCode);
                        throw new BadRequestException("Reset code has expired");
                }

                // 3. Applica la nuova password crittografata
                User user = resetCode.getUser();
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);

                // 4. Invalida (elimina) il codice per non poterlo riutilizzare
                passwordResetRepo.delete(resetCode);

                // 5. Invia notifica di successo
                NotificationDTO passwordResetEmail = NotificationDTO.builder()
                                .recipient(user.getEmail())
                                .subject("Password Updated Successfully")
                                .templateName("password-update-confirmation")
                                .templateVariables(Map.of(
                                                "name", user.getName()))
                                .build();

                notificationService.sendEmail(passwordResetEmail, user);

                return Response.builder()
                                .statusCode(HttpStatus.OK.value())
                                .message("Password updated successfully")
                                .build();

        }

        private void createPatientProfile(User user) {

                Patient patient = Patient.builder()
                                .user(user)
                                .build();
                patientRepo.save(patient);
                log.info("Patient profile created");

        }

        private void createDoctorProfile(RegistrationRequest request, User user) {

                Doctor doctor = Doctor.builder()
                                .specialization(request.getSpecialization())
                                .licenseNumber(request.getLicenseNumber())
                                .user(user)
                                .build();

                doctorRepo.save(doctor);

                log.info("Doctor profile created");
        }

        private void sendRegistrationEmail(RegistrationRequest request, User user) {
                NotificationDTO welcomeEmail = NotificationDTO.builder()
                                .recipient(user.getEmail())
                                .subject("Welcome to DAT Health!")
                                .templateName("welcome")
                                .message("Thank you for registering Your account is ready.")
                                .templateVariables(Map.of(
                                                "name", request.getName(),
                                                "loginLink", loginLink))
                                .build();

                notificationService.sendEmail(welcomeEmail, user);
        }

        private LocalDateTime calculateExpiryDate() {
                return LocalDateTime.now().plusHours(5);
        }

}
