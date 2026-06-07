package com.mediconnect.users.service;

import com.mediconnect.users.repo.PasswordResetRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

// Generatore di codici alfanumerici univoci per il reset password
@Component
@RequiredArgsConstructor
public class CodeGenerator {

    // Repository per i codici di reset password
    private final PasswordResetRepo passwordResetRepo;

    private static final String ALPHA_NUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 5;

    public String generateUniqueCode() {
        String code;
        do {
            code = generateRandomCode();

        } while (passwordResetRepo.findByCode(code).isPresent());

        return code;
    }

    private String generateRandomCode() {
        StringBuilder sb = new StringBuilder(CODE_LENGTH);
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(ALPHA_NUMERIC.length());
            sb.append(ALPHA_NUMERIC.charAt(index));
        }
        return sb.toString();
    }

}
