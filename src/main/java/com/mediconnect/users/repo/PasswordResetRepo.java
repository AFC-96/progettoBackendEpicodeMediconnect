package com.mediconnect.users.repo;

import com.mediconnect.users.entity.PasswordResetCode;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
// Repository JPA codici reset password: ricerca per codice e utente

public interface PasswordResetRepo extends JpaRepository<PasswordResetCode, Long> {
    Optional<PasswordResetCode> findByCode(String code);
    void deleteByUserId(Long userId);
}